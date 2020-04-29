package com.greetify.ecwidtest

import android.graphics.Color
import kotlin.random.Random

/**
 * @property oppositeDirection -  противоположная сторона для направления
 */
abstract class Direction {
    abstract val oppositeDirection: Direction
    /**
     * plusStep - меняем координаты в зависимости от направления
     * @param(model) - объект у которого меняем координаты
     */
    abstract fun plusStep(model: ObjectModel)
}

class TopDirection : Direction() {
    override val oppositeDirection: Direction
        get() = DownDirection()

    override fun plusStep(model: ObjectModel) {
        model.point.y = model.point.y + model.speed
    }
}

class DownDirection : Direction() {
    override val oppositeDirection: Direction
        get() = TopDirection()

    override fun plusStep(model: ObjectModel) {
        model.point.y = model.point.y - model.speed
    }

}

class RightDirection : Direction() {
    override val oppositeDirection: Direction
        get() = LeftDirection()

    override fun plusStep(model: ObjectModel) {
        model.point.x = model.point.x + model.speed
    }

}

class LeftDirection : Direction() {
    override val oppositeDirection: Direction
        get() = RightDirection()

    override fun plusStep(model: ObjectModel) {
        model.point.x = model.point.x - model.speed
    }

}

/**
 * @property direction - направление движения
 */
abstract class Orientation {
    abstract var direction: Direction
    /**
     * changeDirection - смена направления движения у обекта @param(model)
     */
    abstract fun changeDirection(model: ObjectModel)

    /**
     * @fun checkOut - проверка выхода объекта за границы экрана
     * @param objectModel - объект наблюдения
     * @return Boolean - если выходит то @true иначе @false
     */
    abstract fun checkOut(objectModel: ObjectModel): Boolean

    /**
     * @fun move - перемещает объект @param(objectModel)
     */
    abstract fun move(objectModel: ObjectModel)
}

class HorizontalOrientation : Orientation() {
    override var direction: Direction =
        if (Random.nextBoolean()) RightDirection() else LeftDirection()

    override fun changeDirection(model: ObjectModel) {
        direction = direction.oppositeDirection
    }

    override fun checkOut(objectModel: ObjectModel): Boolean {
        return objectModel.point.x - 0 > App.instance.resources.displayMetrics.widthPixels ||
                objectModel.point.x + 0 < 0
    }

    override fun move(objectModel: ObjectModel) {
        direction.plusStep(objectModel)
    }
}

class VerticalOrientation() : Orientation() {
    override var direction: Direction = if (Random.nextBoolean()) TopDirection() else DownDirection()
    override fun changeDirection(model: ObjectModel) {
        direction = direction.oppositeDirection
    }

    override fun checkOut(objectModel: ObjectModel): Boolean {
        return objectModel.point.y + objectModel.size > App.instance.resources.displayMetrics.heightPixels ||
                objectModel.point.y - objectModel.size < 0
    }

    override fun move(objectModel: ObjectModel) {
        direction.plusStep(objectModel)
    }
}

/**
 * @property x - координата  х на экране
 * @property у - координата у на экране
 */
class Point(var x: Float, var y: Float) {
    override fun equals(other: Any?): Boolean {
        return if (other is Point)
            other.x == x && other.y == y
        else false
    }
}


/**
 * @property point: Point - координаты объекта на экране
 * @property size - размер объекта на экране
 * @property speed - скорость объекта
 * @property color - цвет объекта
 * @property orientation - ориентация в каком направлении двигется объект
 */
abstract class ObjectModel {
    var point: Point = Point(0f, 0f)
    open var size = 20
    open var speed = 1
    open var color = Color.parseColor("#ffffff")
    abstract var orientation: Orientation
    /**
     * move - функция для обновления координат объекта на экране
     */
    fun move() {

        if (orientation.checkOut(this))//проверка выходит ли объект за границы экрана
            orientation.changeDirection(this)// если выходит то меняем направление объекта
        orientation.move(this)//передвигаем объект
    }

    /**
     * checkUnion - функция проверки пересечения объектов на плоскости
     * @param(objectModel) - объект с которым идет проверка на пересечение
     */
    fun checkUnion(objectModel: ObjectModel): Boolean {
        val obj1 = this
        val obj2 = objectModel
        return ((obj1.point.x + obj1.size >= obj2.point.x) && (obj1.point.x <= obj2.point.x + obj2.size)
                &&
                ((obj1.point.y + obj1.size >= obj2.point.y) && (obj1.point.y <= obj2.point.y + obj2.size)))
    }

}

/**
 * @property isCatched - флаг проверка на то что, пойман продукт работником или нет
 */
abstract class ProductModel() : ObjectModel() {
    init {
        point.x = Random.nextInt(App.instance.resources.displayMetrics.widthPixels).toFloat()
        point.y = 100f
    }

    var isCatched = false
    override var orientation: Orientation = VerticalOrientation()
}

open class HeavyProductModel() : ProductModel() {
    override var size: Int = super.size * 4
    override var speed: Int = super.speed
    override var color = Color.parseColor("#ff0000")
}

open class LightProductModel() : ProductModel() {
    override var size: Int = super.size * 2
    override var speed: Int = super.size * 3
    override var color = Color.parseColor("#00ff00")

}

open class MediumProductModel() : ProductModel() {
    override var size: Int = super.size * 3
    override var speed: Int = super.size * 2
    override var color = Color.parseColor("#0000ff")

}

abstract class EmployeeModel() : ObjectModel() {
    init {
        point.x = Random.nextInt(App.instance.resources.displayMetrics.widthPixels).toFloat()
        point.y = App.instance.resources.displayMetrics.heightPixels / 2.toFloat()
        speed = Random.nextInt(3, 10)
    }

    override var size: Int = super.size * 5
    override var orientation: Orientation = HorizontalOrientation()
    /**
     * @fun checkCrossingWithProduct - проверка пересечений продуктов и работников
     * @param product - продукт с которым проверяется пересечения,
     */
    abstract fun checkCrossingWithProduct(product: ProductModel): Boolean
}

open class StrongEmployeeModel() : EmployeeModel() {
    override var color = Color.parseColor("#ff00ff")
    override fun checkCrossingWithProduct(product: ProductModel): Boolean {
        return (product is HeavyProductModel || product is MediumProductModel) && checkUnion(product)
    }

}

open class WeakEmployeeModel() : EmployeeModel() {
    override var color = Color.parseColor("#00ff00")
    override fun checkCrossingWithProduct(product: ProductModel): Boolean {
        return product is LightProductModel && checkUnion(product)
    }

}