package de.geobe.raspi.rover

import io.javalin.Javalin
import io.javalin.http.BadRequestResponse
import io.javalin.http.Context
import static io.javalin.apibuilder.ApiBuilder.*

class MotorControlJavalin {

    static boolean isRaspi
    static IMotorControl motorControl

    public static void main(String[] args) {
        def arch = System.getProperty('os.arch')
        isRaspi = arch.startsWith('aarch64')

        if (isRaspi) {
            motorControl = new BasicMotorControl()
        } else {
            motorControl = new MockMotorControl()
        }

        var app = Javalin.create { config ->
            config.router.apiBuilder {
                path('/') {
                    get(hello)
                }
                path('exit') {
                    get(exit)
                    post(exit)
                    path('/{exit}') {
                        get(exit)
                        post(exit)
                    }
                }
                path('{side}') {
                    path('run') {
                        get(run)
                        post('{dir}', run)
                    }
                    path('speed') {
                        get(speed)
                        post('{speed}', speed)
                    }
                    path('frequency') {
                        get(frequency)
                        post('{freq}', frequency)
                    }
                }
            }
        }.start(7070)
    }

    static hello = { Context ctx ->
        ctx.json([hello: 'Hi sagt rover ;)\n'])
    }

    static exit = { Context ctx ->
        def params = ctx.pathParamMap()
        def exit = params ? ctx.pathParam('exit')?.toUpperCase() : null
        if (exit && exit.startsWith('TRUE')) {
            ctx.json(message: 'shutdown server')
            motorControl.shutdown(true)
        } else {
            for (side in [IMotorControl.Side.LEFT, IMotorControl.Side.RIGHT]) {
                motorControl.setMotion(side, IMotorControl.Motion.STOP)
                motorControl.setSpeed(side, 0.0)
                motorControl.setFrequency(side, 0)
            }
            ctx.json([message: 'stop motors'])
        }
    }

    static run = { Context ctx ->
        def params = ctx.pathParamMap()
        def side = ctx.pathParam('side').toUpperCase()
        if (side in ['LEFT', 'RIGHT']) {
            def s = IMotorControl.Side.valueOf(side)
            if (params.containsKey('dir')) {
                def dir = ctx.pathParam('dir').toUpperCase()
                if (dir in ['FORWARD', 'STOP', 'BACKWARD']) {
                    def d = IMotorControl.Motion.valueOf(dir)
                    ctx.json(motorControl.setMotion(s, d))
                } else {
                    throw new BadRequestResponse("bad parameter $dir")
                }
            } else {
                ctx.json(motorControl.getMotion(s))
            }
        } else {
            throw new BadRequestResponse("bad parameter $side")
        }

    }

    static speed = { Context ctx ->
        def params = ctx.pathParamMap()
        def side = ctx.pathParam('side').toUpperCase()
        if (side in ['LEFT', 'RIGHT']) {
            def s = IMotorControl.Side.valueOf(side)
            if(!params.speed) {
                ctx.json(motorControl.getSpeed(s))
            } else {
                def speed = ctx.pathParam('speed')
                if (speed.isFloat()) {
                    def sp = speed.toFloat()
                    ctx.json(motorControl.setSpeed(s, sp))
                } else {
                    throw new BadRequestResponse("bad parameter $speed")
                }
            }
        } else {
            throw new BadRequestResponse("bad parameter $side")
        }
    }

    static frequency = { Context ctx ->
        def side = ctx.pathParam('side').toUpperCase()
        if (side in ['LEFT', 'RIGHT']) {
            def s = IMotorControl.Side.valueOf(side)
            if (ctx.pathParamMap().containsKey('freq') && ctx.pathParam('freq').isInteger()) {
                def freq = ctx.pathParam('freq').toInteger()
                ctx.json(motorControl.setFrequency(s,freq))
            } else {
                ctx.json(motorControl.getFrequency(s))
            }
        } else {
            throw new BadRequestResponse("bad parameter $side")
        }
    }

    private static stringParam(Context ctx, String param) {

    }
}
