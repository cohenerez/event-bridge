package com.erez.loans.bridge.checkpoint;

import com.google.protobuf.ByteString;
import java.util.Optional;

public interface ReplayStore {

    Optional<ByteString> load();

    void save(ByteString replayId);
}
