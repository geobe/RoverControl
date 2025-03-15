package de.geobe.raspi.rover

class MockMotorControl implements IMotorControl {
    def speedList = [
            (Side.LEFT) : 0.0,
            (Side.RIGHT): 0.0
    ]

    MockMotorControl(String s = "") {}

    @Override
    Map setMotion(Side side, Motion motion) {
        [speed: speedList[side], motion: Motion.STOP]
    }

    @Override
    Map getMotion(Side side) {
        [speed: speedList[side], motion: Motion.STOP]
    }

    @Override
    Map setSpeed(Side side = Side.LEFT, float speed) {
        speedList[side] = speed
        [speed: speedList[side], motion: Motion.STOP]
    }

    @Override
    Map getSpeed(Side side) {
        [speed: speedList[side], motion: Motion.STOP]
    }

    Map setFrequency(Side side, int frequency) {
        [frequency: 0]
    }

    Map getFrequency(Side side) {
        [frequency: 0]
    }

    @Override
    void shutdown(boolean exit) {
        if(exit) {
            System.exit(0)
        }
    }
}

