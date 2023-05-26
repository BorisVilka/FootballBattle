package com.footballbattle.game.app

import android.content.Context
import android.graphics.*
import android.hardware.*
import android.media.MediaPlayer
import android.util.AttributeSet
import android.util.Log
import android.view.SurfaceHolder
import android.view.SurfaceView
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import java.util.*
import java.util.concurrent.atomic.AtomicInteger
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

class GameView(val ctx: Context, val attributeSet: AttributeSet): SurfaceView(ctx,attributeSet) {

    var vorotaT = BitmapFactory.decodeResource(ctx.resources,R.drawable.topv)
    var vorotaB = BitmapFactory.decodeResource(ctx.resources,R.drawable.bottomv)
    var enemy = BitmapFactory.decodeResource(ctx.resources,R.drawable.enemy)
    var man = BitmapFactory.decodeResource(ctx.resources,R.drawable.man)
    var ball = BitmapFactory.decodeResource(ctx.resources,R.drawable.ball)

    var bgred = getBitmap(R.drawable.bgred)
    var bggreen = getBitmap(R.drawable.bggreen)

    var goal = BitmapFactory.decodeResource(ctx.resources,R.drawable.goal)
    var goalbg1 = BitmapFactory.decodeResource(ctx.resources,R.drawable.goalbg1)
    var goalbg2 = BitmapFactory.decodeResource(ctx.resources,R.drawable.goalbg2)

    public var mode = 0
    var millis = 0
    var paused = false
    var isWin = false
    private var paintB: Paint = Paint(Paint.DITHER_FLAG)
    private var listener: EndListener? = null
    private var paintFill = Paint().apply {
        color = ctx.getColor(R.color.light_bg)
        style = Paint.Style.FILL
    }
    private var line = Path()
    private var paintStroke = Paint().apply {
        color = ctx.getColor(R.color.light)
        style = Paint.Style.STROKE
        strokeWidth = 40f
    }

    var scoreE = 0
    var scoreM = 0
    private var paintT = Paint().apply {
        textSize = 70f
        color = Color.WHITE
    }

    val updateThread = Thread {
        Timer().schedule(object : TimerTask() {
            override fun run() {
                if (!paused) {
                    millis ++
                    update.run()
                }
            }
        }, 500, 16)
    }

    var center = Path()

    var ex = 0f
    var ey = 0f

    var mx = 0f
    var my = 0f

    var bx = 0f
    var by = 0f

    var dx = MutableLiveData(0f)
    var dy = MutableLiveData(0f)


    var first1 = false
    var array = FloatArray(3)
    var delta = 1.5f
    val service = ctx.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    val sensor = service.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

    var music = ctx.getSharedPreferences("prefs",Context.MODE_PRIVATE).getBoolean("music",false)
    var sounds = ctx.getSharedPreferences("prefs",Context.MODE_PRIVATE).getBoolean("sound",false)

    var player = MediaPlayer.create(ctx,R.raw.bg)
    var goalS = MediaPlayer.create(ctx,R.raw.goal)
    var kick = MediaPlayer.create(ctx,R.raw.kick)
    var lose = MediaPlayer.create(ctx,R.raw.lose)
    var win = MediaPlayer.create(ctx,R.raw.win)

    private fun getBitmap(drawableRes: Int): Bitmap? {
        val drawable = ctx.resources.getDrawable(drawableRes)
        val canvas = Canvas()
        val bitmap = Bitmap.createBitmap(
            drawable.intrinsicWidth,
            drawable.intrinsicHeight,
            Bitmap.Config.ARGB_8888
        )
        canvas.setBitmap(bitmap)
        drawable.setBounds(0, 0, drawable.intrinsicWidth, drawable.intrinsicHeight)
        drawable.draw(canvas)
        return bitmap
    }

    init {
        player.setOnCompletionListener {
            it.start()
        }
        if(music) player.start()
        first1 = true
        val list = object : SensorEventListener {
            override fun onSensorChanged(event: SensorEvent?) {
                if(abs(array[0]- event!!.values[0])>=delta || abs(array[1]- event.values[1])>=delta) {
                   // Log.d("TAG",event?.values.contentToString()+" "+array.contentToString()+" "+abs(array[0]-event!!.values[0]).toString()+" "+abs(array[1]- event.values[1]).toString()+" "+delta.toString())
                    array = event.values.clone()
                    dx.postValue(-array[0])
                    dy.postValue(array[1])
                }
            }

            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {

            }


        }
        service.registerListener(list,sensor,SensorManager.SENSOR_DELAY_NORMAL)

        ball = Bitmap.createScaledBitmap(ball,ball.width/3,ball.height/3,true)
        vorotaB = Bitmap.createScaledBitmap(vorotaB,vorotaB.width/3,vorotaB.height/3,true)
        vorotaT = Bitmap.createScaledBitmap(vorotaT,vorotaT.width/3,vorotaT.height/3,true)
        enemy = Bitmap.createScaledBitmap(enemy,enemy.width/3,enemy.height/3,true)
        man = Bitmap.createScaledBitmap(man,man.width/3,man.height/3,true)

        goal = Bitmap.createScaledBitmap(goal,goal.width/3,goal.height/3,true)
        goalbg1 = Bitmap.createScaledBitmap(goalbg1,goalbg1.width/3,goalbg1.height/3,true)
        goalbg2 = Bitmap.createScaledBitmap(goalbg2,goalbg2.width/3,goalbg2.height/3,true)

        holder.addCallback(object : SurfaceHolder.Callback{
            override fun surfaceCreated(holder: SurfaceHolder) {

            }

            override fun surfaceChanged(
                holder: SurfaceHolder,
                format: Int,
                width: Int,
                height: Int
            ) {
                val canvas = holder.lockCanvas()
                if(canvas!=null) {
                    ex = canvas.width/2f-enemy.width/2f
                    ey = 20f+vorotaT.height

                    mx = canvas.width/2f-man.width/2f
                    my = canvas.height-canvas.height/16f-vorotaB.height

                    by = canvas.height/2f-ball.height/2f
                    bx = canvas.width/2f-ball.width/2f

                    center.moveTo(canvas.width/3f,0f)
                    center.lineTo(canvas.width/3f*2,0f)
                    center.lineTo(canvas.width/3f*2,canvas.height.toFloat())
                    center.lineTo(canvas.width/3f,canvas.height.toFloat())
                    center.close()
                    line.moveTo(0f,canvas.height/2f)
                    line.lineTo(canvas.width/2f-canvas.width/12f,canvas.height/2f)
                    line.moveTo(canvas.width/2f+canvas.width/12f,canvas.height/2f)
                    line.lineTo(canvas.width.toFloat(),canvas.height/2f)
                    draw(canvas)
                    holder.unlockCanvasAndPost(canvas)
                }
            }

            override fun surfaceDestroyed(holder: SurfaceHolder) {
                  paused = true
                 updateThread.interrupt()
                service.unregisterListener(list)
                player.release()
                try {
                    kick.release()
                    win.release()
                    lose.release()
                    goalS.release()
                } catch (e:Exception) {
                    e.printStackTrace()
                }

            }

        })
        updateThread.start()

    }

    var draw = false
    var en = false

    val update = Runnable{
        var isEnd = false
        try {
            val canvas = holder.lockCanvas()
           // Log.d("TAG","${dx.value} ${dy.value}")
            if(draw) {
                if(millis>=150) {
                    millis = 0
                    draw = false
                }
            } else {
                if(bx<=0 || bx>=canvas.width-ball.width) {
                    dx.postValue(-dx.value!!)
                    bx += -dx.value!!
                }
                else bx += dx.value!!
                by += dy.value!!
                bx = min(canvas.width.toFloat()-ball.width,bx)
                bx = max(0f,bx)
                by = min(canvas.height.toFloat()-ball.height,by)
                by = max(0f,by)
            }
            if(by<canvas.height/2f) {
                if(dx.value!! <0f) ex -=2f
                else ex += 2f
                ex = max(canvas.width/2f-vorotaT.width/2f,ex)
                ex = min(canvas.width/2f+vorotaT.width/2f-enemy.width,ex)
                if(ey-by<=enemy.height && ey-by>0 && (bx-ex>0 && bx-ex<=ball.width || ex-bx>0 && ex-bx<=enemy.width)) {
                    dy.postValue(-dy.value!!)
                    by += -dy.value!!
                    dx.postValue(-dx.value!!)
                    bx += -dx.value!!
                    try {
                        if(sounds) {
                            kick.seekTo(0)
                            kick.start()
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
                if(bx>=canvas.width/2f-vorotaT.width/2f+vorotaT.width/8f && bx<=canvas.width/2f+vorotaT.width/2f-vorotaT.width/8f-ball.width && by<vorotaT.height/5f*4) {
                    array = FloatArray(3)
                    dy.postValue(0f)
                    dx.postValue(0f)
                    by = canvas.height/2f-ball.height/2f
                    bx = canvas.width/2f-ball.width/2f
                    scoreM++
                    en = false
                    millis = 0
                    draw = true
                    if(mode==1) {
                        val f = ctx.getSharedPreferences("prefs", Context.MODE_PRIVATE).getString("endless","0/0")
                        val a = f!!.split("/")[0].toInt()

                        if(a<=scoreM) ctx.getSharedPreferences("prefs", Context.MODE_PRIVATE).edit().putString("endless","$scoreM/$scoreE").apply()
                    }
                    try {
                        if(sounds) {
                            if(mode==0 && scoreE+scoreM==5) {
                                if(scoreE>scoreM) {
                                    lose.seekTo(0)
                                    lose.start()
                                } else {
                                    win.seekTo(0)
                                    win.start()
                                }
                            } else {
                                goalS.seekTo(0)
                                goalS.start()
                            }
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            } else if(by>canvas.height/2f-ball.height) {
                if(dx.value!! <0f) mx -=2f
                else mx += 2f
                mx = max(canvas.width/2f-vorotaB.width/2f,mx)
                mx = min(canvas.width/2f+vorotaB.width/2f-man.width,mx)
                if(by-my>=-ball.height && by-my<0 && (bx-mx>0 && bx-mx<=ball.width || mx-bx>0 && mx-bx<=man.width)) {
                    dy.postValue(-dy.value!!)
                    by += -dy.value!!
                    dx.postValue(-dx.value!!)
                    bx += -dx.value!!
                    try {
                        if(sounds) {
                            kick.seekTo(0)
                            kick.start()
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
                if(bx>=canvas.width/2f-vorotaB.width/2f+vorotaB.width/8f && bx<=canvas.width/2f+vorotaB.width/2f-vorotaB.width/8f-ball.width && by>canvas.height-vorotaB.height/5f*4) {
                    array = FloatArray(3)
                    dy.postValue(0f)
                    dx.postValue(0f)
                    by = canvas.height/2f-ball.height/2f
                    bx = canvas.width/2f-ball.width/2f
                    scoreE++
                    millis = 0
                    en = true
                    draw = true
                    if(mode==1) {
                        val f = ctx.getSharedPreferences("prefs", Context.MODE_PRIVATE).getString("endless","0/0")
                        val a = f!!.split("/")[0].toInt()
                        if(a<scoreM)ctx.getSharedPreferences("prefs", Context.MODE_PRIVATE).edit().putString("endless","$scoreM/$scoreE").apply()
                    }
                    try {
                        if(sounds) {
                            if(mode==0 && scoreE+scoreM==5) {
                                if(scoreE>scoreM) {
                                    lose.seekTo(0)
                                    lose.start()
                                } else {
                                    win.seekTo(0)
                                    win.start()
                                }
                            } else {
                                goalS.seekTo(0)
                                goalS.start()
                            }
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
            canvas.drawColor(ctx.getColor(R.color.bg))
            canvas.drawPath(center,paintFill)
            canvas.drawCircle(canvas.width/2f,canvas.height/2f,canvas.width/12f,paintStroke)
            canvas.drawCircle(canvas.width/2f,canvas.height/2f,canvas.width/3f,paintStroke)
            canvas.drawPath(line,paintStroke)
            canvas.drawBitmap(vorotaB,canvas.width/2f-vorotaB.width/2f,canvas.height-canvas.height/8f,paintB)
            canvas.drawBitmap(vorotaT,canvas.width/2f-vorotaT.width/2f,20f,paintB)
            canvas.drawBitmap(enemy,ex,ey,paintB)
            canvas.drawBitmap(man,mx,my,paintB)
            canvas.drawBitmap(ball,bx,by,paintB)
            canvas.drawBitmap(bgred!!,canvas.width/2f-bgred!!.width/2f,20f,paintB)
            canvas.drawText("$scoreE",canvas.width/2f-20f, bgred!!.height.toFloat()-20f,paintT)
            canvas.drawBitmap(bggreen!!,canvas.width/2f-bgred!!.width/2f,canvas.height- bggreen!!.height-20f,paintB)
            canvas.drawText("$scoreM",canvas.width/2f-20f, canvas.height-10f- bggreen!!.height/3f,paintT)
            if(draw) {
                canvas.drawBitmap(if(en) goalbg2 else goalbg1,canvas.width/2f-goalbg1.width/2f,canvas.height/2f-goalbg1.height/2f,paintB)
                canvas.drawBitmap(goal,canvas.width/2f-goal.width/2f,canvas.height/2f-goal.height/2f,paintB)
            }
            holder.unlockCanvasAndPost(canvas)
            if(mode==0 && scoreE+scoreM==5) {
                togglePause()
                isEnd = true
            }
            if(isEnd) {
                if(listener!=null) listener!!.end()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun setEndListener(list: EndListener) {
        this.listener = list
    }
    fun togglePause() {
        paused = !paused
        /*if(music) {
            try {
                if(paused) player.pause()
                else player.start()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }*/
    }

    companion object {
        var st = FloatArray(3)
        interface EndListener {
            fun end();
            fun score(score: Int);
        }

    }

}