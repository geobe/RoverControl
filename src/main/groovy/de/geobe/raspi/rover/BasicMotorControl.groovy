package de.geobe.raspi.rover

import com.pi4j.io.gpio.digital.DigitalOutput
import com.pi4j.io.gpio.digital.DigitalState
import com.pi4j.io.pwm.Pwm

class BasicMotorControl implements IMotorControl {

    Map<Side, Pwm> pwm
    Map<Side, List<DigitalOutput>> dout
    def motion = [
            (Side.LEFT) : Motion.STOP,
            (Side.RIGHT): Motion.STOP
    ]

    Map<Side, Float> speed = [
            (Side.LEFT) : 0.0f,
            (Side.RIGHT): 0.0f
    ]

    def freq = [
            (Side.LEFT): 2,
            (Side.RIGHT): 2
    ]

    BasicMotorControl() {
        pwm = [
                (Side.LEFT) : GpioConfigService.createPwm(12),
                (Side.RIGHT): GpioConfigService.createPwm(13)
        ]
        println 'pwm pins created'
        dout = [
                (Side.LEFT) : [GpioConfigService.createDigitalOutput(23, DigitalState.LOW, DigitalState.LOW),
                               GpioConfigService.createDigitalOutput(24, DigitalState.LOW, DigitalState.LOW)],
                (Side.RIGHT): [GpioConfigService.createDigitalOutput(17, DigitalState.LOW, DigitalState.LOW),
                               GpioConfigService.createDigitalOutput(27, DigitalState.LOW, DigitalState.LOW)]
        ]
        println 'output pins created'
        pwm.each { pwmPin ->
            pwmPin.value.off()
        }
    }

    private Map forward(Side side) {
        def pins = dout[side]
        pins[0].high()
        pins[1].low()
        if (speed[side] > 0) {
            pwm[side].on(speed[side], freq[side])
        }
        [speed: speed[side], motion: motion[side]]
    }

    private Map reverse(Side side) {
        def pins = dout[side]
        pins[0].low()
        pins[1].high()
        if (speed[side] > 0) {
            pwm[side].on(speed[side], freq[side])
        }
        [speed: speed[side], motion: motion[side]]
    }

    private Map stop(Side side) {
        def pins = dout[side]
        pins[0].low()
        pins[1].low()
        pwm[side].off()
        [speed: speed[side], motion: motion[side]]
    }

    @Override
    Map setMotion(Side side, Motion motion) {
        this.motion[side] = motion
        switch (motion) {
            case Motion.FORWARD:
                return forward(side)
            case Motion.STOP:
                return stop(side)
            case Motion.BACKWARD:
                return reverse(side)
        }
        [speed: speed[side], motion: motion[side]]
    }

    @Override
    Map getMotion(Side side) {
        [speed: speed[side], motion: motion[side]]
    }

    @Override
    Map setSpeed(Side side, float newSpeed) {
        def sp = (float) Math.max(0.0, Math.min(100.0, newSpeed))
        speed[side] = sp
        if (speed[side] > 0) {
            pwm[side].on(speed[side], freq4speed(speed[side], side))
        }
        [speed: speed[side], motion: motion[side]]
    }

    @Override
    Map getSpeed(Side side) {
        [speed: speed[side], motion: motion[side]]
    }

    Map setFrequency(Side side, int frequency) {
        freq[side] = frequency
        if (speed[side] > 0) {
            pwm[side].on(speed[side], freq[side])
        }
        [frequency: freq[side]]
    }

    Map getFrequency(Side side) {
        [frequency: freq[side]]
    }

    @Override
    void shutdown(boolean exit = true) {
        def ctx = GpioConfigService.context
        pwm.each {
            it.value.off()
            it.value.shutdown(ctx)
        }
        def pins = dout[Side.LEFT]
        pins[0].shutdown(ctx)
        pins[1].shutdown(ctx)
        pins = dout[Side.RIGHT]
        pins[0].shutdown(ctx)
        pins[1].shutdown(ctx)
        ctx.shutdown()
        if (exit) {
            System.exit(0)
        }
    }

    private int freq4speed(float speed, Side side) {
        def result
        if (speed < 7f)
            result = 2
        else if (speed < 14f)
            result = 4
        else if (speed < 21f)
            result = 8
        else if (speed < 28f)
            result = 16
        else if (speed < 51f)
            result = 32
        else
            result = 64
        freq[side] = result
        result
    }

    static void main(String[] args) {
        def mc = new BasicMotorControl()
        println 'BasicMotorControl created'
        println "get speed left ${mc.getSpeed(Side.LEFT)}, right ${mc.getSpeed(Side.RIGHT)}"
        float sp = 66.0f
        println "set speed left ${mc.setSpeed(Side.LEFT, sp)}, right ${mc.setSpeed(Side.RIGHT, sp)}"
        println "set motion left ${mc.setMotion(Side.LEFT, Motion.FORWARD)}, right ${mc.setMotion(Side.RIGHT, Motion.BACKWARD)}"
        Thread.sleep(5000)
//        println "speed left ${mc.getSpeed(Side.LEFT)}, right ${getSpeed(Side.RIGHT)}"
        mc.shutdown()
    }

}
