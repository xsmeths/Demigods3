package com.demigodsrpg.demigods.engine.exception;

public class SpigotNotFoundException extends IllegalArgumentException {
    public SpigotNotFoundException() {
        super("Spigot is not installed.");
    }
}
