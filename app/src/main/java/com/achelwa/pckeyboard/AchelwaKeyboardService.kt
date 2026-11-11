package com.achelwa.pckeyboard

import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.inputmethodservice.InputMethodService
import android.view.Gravity
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.HorizontalScrollView
import android.widget.LinearLayout
import android.widget.TextView

class AchelwaKeyboardService : InputMethodService() {
    private var controlHeld = false
    private var shiftHeld = false
    private var altHeld = false
    private var capsLock = false
    private val bg = Color.rgb(18, 21, 27)
    private val key = Color.rgb(43, 49, 60)
    private val modifier = Color.rgb(54, 70, 92)
    private val accent = Color.rgb(38, 125, 185)

    override fun onCreateInputView(): View = buildKeyboard()

    override fun onStartInput(attribute: EditorInfo?, restarting: Boolean) {
        super.onStartInput(attribute, restarting)
        controlHeld = false
        shiftHeld = false
        altHeld = false
    }

    private fun buildKeyboard(): View {
        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(4), dp(6), dp(4), dp(5))
            setBackgroundColor(bg)
        }
        addRow(root, listOf("ESC", "F1", "F2", "F3", "F4", "F5", "F6", "F7", "F8", "F9", "F10", "F11", "F12"))
        addRow(root, listOf("~\n`", "!\n1", "@\n2", "#\n3", "$\n4", "%\n5", "^\n6", "&\n7", "*\n8", "(\n9", ")\n0", "_\n-", "+\n=", "⌫"))
        addRow(root, listOf("TAB", "Q", "W", "E", "R", "T", "Y", "U", "I", "O", "P", "{\n[", "}\n]", "|\n\\"))
        addRow(root, listOf("CAPS", "A", "S", "D", "F", "G", "H", "J", "K", "L", ":\n;", "\"\n'", "ENTER"))
        addRow(root, listOf("SHIFT", "Z", "X", "C", "V", "B", "N", "M", "<\n,", ">\n.", "?\n/", "SHIFT"))
        addRow(root, listOf("CTRL", "⊞", "ALT", "SPACE", "ALT", "CTRL", "←", "↑", "↓", "→"))
        addRow(root, listOf("HOME", "END", "INS", "DEL", "PGUP", "PGDN", "CTRL+C", "CTRL+V", "CTRL+X", "CTRL+A"))
        return root
    }

    private fun addRow(parent: LinearLayout, labels: List<String>) {
        val scroller = HorizontalScrollView(this).apply {
            isHorizontalScrollBarEnabled = false
            overScrollMode = View.OVER_SCROLL_NEVER
        }
        val row = LinearLayout(this).apply { orientation = LinearLayout.HORIZONTAL; gravity = Gravity.CENTER_VERTICAL }
        labels.forEach { label ->
            val weight = if (label == "SPACE") 3.5f else if (label in setOf("ENTER", "SHIFT", "TAB", "CTRL", "ALT", "CAPS", "⌫")) 1.45f else 1f
            val button = TextView(this).apply {
                text = label
                gravity = Gravity.CENTER
                setTextColor(Color.WHITE)
                textSize = if (label.length > 4 || label.contains('\n')) 10f else 11f
                typeface = Typeface.create("sans-serif-medium", Typeface.NORMAL)
                maxLines = 2
                background = rounded(if (isModifier(label)) modifier else if (label.startsWith("CTRL+")) accent else key)
                setPadding(dp(2), dp(1), dp(2), dp(1))
                setOnClickListener { handle(label) }
            }
            row.addView(button, LinearLayout.LayoutParams(0, dp(39), weight).apply { setMargins(dp(1), dp(2), dp(1), dp(2)) })
        }
        scroller.addView(row)
        parent.addView(scroller, LinearLayout.LayoutParams(-1, dp(43)))
    }

    private fun isModifier(label: String) = label in setOf("ESC", "TAB", "CTRL", "ALT", "SHIFT", "CAPS", "⊞", "SPACE")

    private fun rounded(color: Int) = GradientDrawable().apply {
        setColor(color)
        cornerRadius = dp(5).toFloat()
        setStroke(dp(1), Color.rgb(67, 75, 89))
    }

    private fun handle(label: String) {
        val ic = currentInputConnection ?: return
        when (label) {
            "CTRL" -> { controlHeld = !controlHeld; return }
            "CTRL+C" -> { ic.performContextMenuAction(android.R.id.copy); controlHeld = false }
            "CTRL+V" -> { ic.performContextMenuAction(android.R.id.paste); controlHeld = false }
            "CTRL+X" -> { ic.performContextMenuAction(android.R.id.cut); controlHeld = false }
            "CTRL+A" -> { ic.performContextMenuAction(android.R.id.selectAll); controlHeld = false }
            "SHIFT" -> { shiftHeld = !shiftHeld; return }
            "ALT" -> { altHeld = !altHeld; return }
            "CAPS" -> { capsLock = !capsLock; return }
            "SPACE" -> ic.commitText(" ", 1)
            "TAB" -> ic.commitText("\t", 1)
            "ENTER" -> {
                val action = currentInputEditorInfo?.imeOptions?.and(EditorInfo.IME_MASK_ACTION) ?: EditorInfo.IME_ACTION_NONE
                if (action == EditorInfo.IME_ACTION_NONE || !ic.performEditorAction(action)) sendKey(66)
            }
            "⌫" -> sendKey(67)
            "DEL" -> sendKey(112)
            "ESC" -> sendKey(111)
            "HOME" -> sendKey(122)
            "END" -> sendKey(123)
            "INS" -> sendKey(124)
            "⊞" -> Unit
            "PGUP" -> sendKey(92)
            "PGDN" -> sendKey(93)
            "F1" -> sendKey(131)
            "F2" -> sendKey(132)
            "F3" -> sendKey(133)
            "F4" -> sendKey(134)
            "F5" -> sendKey(135)
            "F6" -> sendKey(136)
            "F7" -> sendKey(137)
            "F8" -> sendKey(138)
            "F9" -> sendKey(139)
            "F10" -> sendKey(140)
            "F11" -> sendKey(141)
            "F12" -> sendKey(142)
            "←" -> sendDirectional(21)
            "→" -> sendDirectional(22)
            "↑" -> sendDirectional(19)
            "↓" -> sendDirectional(20)
            else -> {
                var output = if (label.contains('\n')) {
                    if (shiftHeld) label.substringBefore('\n') else label.substringAfter('\n')
                } else label
                if (output.length == 1 && output[0].isLetter() && (shiftHeld xor capsLock)) output = output.uppercase()
                if (controlHeld && output.length == 1 && output[0].isLetter()) {
                    ic.commitText((output[0].uppercaseChar() - 'A' + 1).toChar().toString(), 1)
                } else ic.commitText(output, 1)
                controlHeld = false
                shiftHeld = false
                altHeld = false
            }
        }
    }

    private fun sendDirectional(code: Int) = sendKey(code)

    private fun sendKey(code: Int) {
        val meta = (if (controlHeld) android.view.KeyEvent.META_CTRL_ON or android.view.KeyEvent.META_CTRL_LEFT_ON else 0) or
            (if (shiftHeld) android.view.KeyEvent.META_SHIFT_ON or android.view.KeyEvent.META_SHIFT_LEFT_ON else 0) or
            (if (altHeld) android.view.KeyEvent.META_ALT_ON or android.view.KeyEvent.META_ALT_LEFT_ON else 0)
        currentInputConnection?.let { ic ->
            ic.sendKeyEvent(android.view.KeyEvent(android.view.KeyEvent.ACTION_DOWN, code, 0, meta))
            ic.sendKeyEvent(android.view.KeyEvent(android.view.KeyEvent.ACTION_UP, code, 0, meta))
        }
        controlHeld = false
        shiftHeld = false
        altHeld = false
    }

    private fun dp(value: Int) = (value * resources.displayMetrics.density).toInt()
}
