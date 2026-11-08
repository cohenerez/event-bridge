package com.erez.loans.bridge.checkpoint;

import com.google.protobuf.ByteString;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Base64;
import java.util.Optional;

/**
 * Keeps the replay ID in a small file (Base64). Writes go to a temp file that is then moved into
 * place, so a crash mid-write never leaves a corrupt checkpoint. Salesforce retains platform events
 * for 72 hours, so the bridge can be down that long without losing events.
 */
public class FileReplayStore implements ReplayStore {

    private final Path file;

    public FileReplayStore(Path file) {
        this.file = file;
    }

    @Override
    public synchronized Optional<ByteString> load() {
        try {
            if (!Files.exists(file)) {
                return Optional.empty();
            }
            String encoded = Files.readString(file, StandardCharsets.UTF_8).trim();
            return encoded.isEmpty() ? Optional.empty() : Optional.of(ByteString.copyFrom(Base64.getDecoder().decode(encoded)));
        } catch (IOException | IllegalArgumentException e) {
            return Optional.empty();
        }
    }

    @Override
    public synchronized void save(ByteString replayId) {
        try {
            Path parent = file.toAbsolutePath().getParent();
            if (parent != null) {
                Files.createDirectories(parent);
            }
            Path temp = Files.createTempFile(parent, "replay-", ".tmp");
            Files.writeString(temp, Base64.getEncoder().encodeToString(replayId.toByteArray()), StandardCharsets.UTF_8);
            Files.move(temp, file, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
        } catch (IOException e) {
            throw new UncheckedIOException("Cannot save replay ID to " + file, e);
        }
    }
}
