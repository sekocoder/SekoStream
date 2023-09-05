package com.example.sekostream.media;

public interface PackableEx extends Packable {
    void unmarshal(ByteBuf in);
}
