package no.uio.ifi.in2000.team39.ui.map.components

// AddressInputValidator. Used to validate user input in the address input field.

object AddressInputValidator {

    fun isPositiveDouble(input: String): Boolean =
        input.toDoubleOrNull()?.let { it > 0 } ?: false

    fun isAngleValid(input: String): Boolean =
        input.toIntOrNull()?.let { it in 0..180 } ?: false

    fun isDirectionValid(input: String): Boolean =
        input.toIntOrNull()?.let { it in 0..359 } ?: false

    fun clampAngle(input: String): String =
        input.toIntOrNull()?.coerceIn(0, 180)?.toString() ?: ""

    fun clampDirection(input: String): String =
        input.toIntOrNull()?.coerceIn(0, 359)?.toString() ?: ""

    fun isDigitsOnly(input: String): Boolean =
        input.all { it.isDigit() }

    fun isDigitsOrDot(input: String): Boolean =
        input.all { it.isDigit() || it == '.' }
}
