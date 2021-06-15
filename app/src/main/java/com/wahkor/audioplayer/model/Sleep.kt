package com.wahkor.audioplayer.model


data class Sleep(
    var oneTimeId:Long,
    var repeatTimeId:Long,
    var isRepeat: Boolean,
    var delayTime: Int ,
    var sleepTime: Int ,
    var wakeupTime: Int
) {
    fun getRealDelay(minutes: Int): Long {
        return System.currentTimeMillis()+(minutes*60*1000)
    }

    val sleepToString: String
        get() {
            val hours = sleepTime / 60
            val minute = sleepTime - hours * 60
            var string = if (hours < 10) "0$hours:" else "$hours:"
            string += if (minute < 10) "0$minute" else "$minute"
            return string
        }
    val wakeupToString: String
        get() {
            val hours = wakeupTime / 60
            val minute = wakeupTime - hours * 60
            var string = if (hours < 10) "0$hours:" else "$hours:"
            string += if (minute < 10) "0$minute" else "$minute"
            return string
        }
    /*
    val getRepeatTimeDelay: Long
        get() {
            var sleepWithIn = 0
            var wakeupWithIn = 0
            val now = System.currentTimeMillis()
            tm.convertFromMinute(sleepTime) { hours, minutes ->
                sleepWithIn = tm.getMinuteDifferent(hours, minutes)
            }
            tm.convertFromMinute(wakeupTime) { hours, minutes ->
                wakeupWithIn = tm.getMinuteDifferent(hours, minutes)
            }
            val minutes = if (sleepWithIn < wakeupWithIn) {
                sleepWithIn + delayTime
            } else {
                delayTime
            }
            return now + (minutes * 1000 * 60).toLong()

        }*/
}

