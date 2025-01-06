package com.example.stopwatchproject.common

data class MilitaryTime(val hour: String, val minute: String, val second: String) {
    override fun toString(): String {
        return "$hour:$minute:$second"
    }

    companion object {
        private const val SECONDS_IN_HOUR = 3600
        private const val SECONDS_IN_MINUTE = 60

        private fun timeUnitToString(timeUnit: Long): String {
            return if (timeUnit < 10) "0$timeUnit" else timeUnit.toString()
        }

        fun secondsToMilitaryTime(timeInSeconds: Long): MilitaryTime {
            val hours = timeInSeconds / SECONDS_IN_HOUR
            val minutes = timeInSeconds % SECONDS_IN_HOUR / SECONDS_IN_MINUTE
            val seconds = timeInSeconds % SECONDS_IN_MINUTE

            return MilitaryTime(
                timeUnitToString(hours),
                timeUnitToString(minutes),
                timeUnitToString(seconds)
            )
        }
    }
}
