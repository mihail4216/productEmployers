package com.greetify.ecwidtest

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.view.SurfaceHolder
import android.view.SurfaceView


class ProductSurfaceView(context: Context) : SurfaceView(context), SurfaceHolder.Callback {

    init {
        holder.addCallback(this)
    }

    private lateinit var drawThread: DrawThread
    override fun surfaceChanged(holder: SurfaceHolder?, p1: Int, p2: Int, p3: Int) {
    }

    override fun surfaceDestroyed(p0: SurfaceHolder?) {
        drawThread.isRunning = false
    }

    override fun surfaceCreated(holder: SurfaceHolder?) {
        holder?.also {
            drawThread = DrawThread(it)
            drawThread.start()
        }
    }


}


class DrawThread() : Thread() {
    private lateinit var mSurfaceHolder: SurfaceHolder
    var isRunning = true

    private val mArrayProductObjects = arrayListOf<ProductModel>()
    private val mArrayEmployeeObjects = arrayListOf<EmployeeModel>()
    private var isFirstLaunch = true

    constructor(surfaceHolder: SurfaceHolder) : this() {
        this.mSurfaceHolder = surfaceHolder
    }

    override fun run() {
        super.run()
        while (isRunning) {
            var canvas: Canvas? = null
            try {
                canvas = mSurfaceHolder.lockCanvas() //получаем canvas
                synchronized(mSurfaceHolder) {
                    draw(canvas) //функция рисования
                }
            } catch (e: NullPointerException) { /*если canvas не доступен*/
            } finally {
                if (canvas != null) mSurfaceHolder.unlockCanvasAndPost(canvas) //освобождаем canvas
            }
            synchronized(mArrayProductObjects) {
                // очищаем список от пойманых продуктов
                mArrayProductObjects.removeAll(mArrayProductObjects.filter { it.isCatched })
            }
            sleep(50)

        }
    }

    @SuppressLint("RestrictedApi")
    private fun draw(canvas: Canvas) {
        canvas.drawColor(Color.BLACK)
        // инитим объекты при первом запуске
        if (isFirstLaunch)
            init()
        // перерисовка объектов
        updateObjects(canvas)
    }

    private fun updateObjects(canvas: Canvas) {
        synchronized(mArrayProductObjects) {
            for (obj in mArrayProductObjects) {
                drawProduct(canvas, obj)
                obj.move()
            }
        }
        for (obj in mArrayEmployeeObjects) {
            drawEmployees(canvas, obj)
            obj.move()
        }
    }

    /**
     * рисуется продукт(круг) цвет и размер зависит от продукта,
     * чем больше круг тем от тужелее и медленее.
     * цвет:
     *      красный - тяжелый
     *      синий - средний
     *      зеленый - легкий
     *
     */
    private fun drawProduct(canvas: Canvas, product: ProductModel) {
        // если продукт не пойман то рисуем его
        if (!product.isCatched) {
            val paint = Paint()
            paint.isAntiAlias = true
            paint.style = Paint.Style.FILL
            paint.color = product.color
            canvas.drawCircle(product.point.x, product.point.y, product.size.toFloat(), paint)
            // проверка продукта на пересечения с работниками
            for (employer in mArrayEmployeeObjects) {
                product.isCatched = employer.checkCrossingWithProduct(product)
                if (product.isCatched)
                    return
            }
        }
    }

    /**
     * рисуется работник(квадрат) цвет зависит от тип работника
     * цвет:
     *      зеленый - слабый работник
     *      фиолетовый - сильный работник
     */
    private fun drawEmployees(canvas: Canvas, obj: EmployeeModel) {
        val paint = Paint()
        paint.isAntiAlias = true
        paint.style = Paint.Style.FILL
        paint.color = obj.color
        canvas.drawRect(
            Rect(
                (obj.point.x - obj.size / 2).toInt(),
                (obj.point.y - obj.size / 2).toInt(),
                (obj.point.x + obj.size / 2).toInt(),
                (obj.point.y + obj.size / 2).toInt()

            ), paint
        )
    }

    private fun init() {
        isFirstLaunch = false
        startThreadInitProductEvery10Sec()
        mArrayProductObjects.add(HeavyProductModel())
        mArrayProductObjects.add(MediumProductModel())
        mArrayProductObjects.add(LightProductModel())
        mArrayEmployeeObjects.add(StrongEmployeeModel())
        mArrayEmployeeObjects.add(WeakEmployeeModel())
    }

    /**
     * Поток создает продукты каждые 10 секунд
     */
    private fun startThreadInitProductEvery10Sec() {
        Thread {
            while (isRunning) {
                sleep(10000)
                synchronized(mArrayProductObjects) {
                    for (i in 0..3) mArrayProductObjects.add(LightProductModel())
                    for (i in 0..3) mArrayProductObjects.add(MediumProductModel())
                    for (i in 0..1) mArrayProductObjects.add(HeavyProductModel())
                }
            }
        }.start()
    }

}
