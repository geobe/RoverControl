package de.geobe.raspi.rover;

import java.util.Map;

/**
 * Interface for controlling one or two motors with a L298N on a raspberry pi
 */
interface IMotorControl {
    enum Side {
        LEFT,
        RIGHT
    }

    enum Motion {
        FORWARD,
        STOP,
        BACKWARD
    }

    /**
     * Setze die Bewegungsrichtung: Vorwärts | Stop | Rückwärts
     * @param side Auswahl Motor Links oder Rechts
     * @param motion Bewegungsrichtung
     * @return aktuelle PWM Geschwindigkeitseinstellung
     */
    Map setMotion(Side side, Motion motion);
    Map getMotion(Side side);
    Map setSpeed(Side side, float speed);
    Map getSpeed(Side side);
    Map setFrequency(Side side, int frequency);
    Map getFrequency(Side side);

    /**
     * Steuerprogramm ordentlich beenden, alle Steuerungssignale auf dem Raspberry Pi
     * auf neutral setzen
     */
    void shutdown(boolean exit);
}
