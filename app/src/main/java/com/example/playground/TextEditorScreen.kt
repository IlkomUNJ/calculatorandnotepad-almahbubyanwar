package com.example.playground


import android.content.ClipData
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.outlined.NoteAdd
import androidx.compose.material.icons.outlined.ContentCopy
import androidx.compose.material.icons.outlined.ContentCut
import androidx.compose.material.icons.outlined.ContentPaste
import androidx.compose.material.icons.outlined.FormatBold
import androidx.compose.material.icons.outlined.FormatClear
import androidx.compose.material.icons.outlined.FormatItalic
import androidx.compose.material.icons.outlined.NoteAdd
import androidx.compose.material.icons.outlined.Save
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MediumTopAppBar
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ModifierLocalBeyondBoundsLayout
import androidx.compose.ui.platform.ClipEntry
import androidx.compose.ui.platform.Clipboard
import androidx.compose.ui.platform.ClipboardManager
import androidx.compose.ui.platform.LocalClipboard
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.Paragraph
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.substring
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat.getSystemService
import androidx.core.text.isDigitsOnly
import kotlinx.coroutines.selects.select
import kotlin.math.sign

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TextEditor(modifier: Modifier, onNavigateBack: () -> Unit) {
    var fieldText by remember {
        mutableStateOf(
            TextFieldValue(
                annotatedString = buildAnnotatedString {
                    append("")
                }
            )
        )
    }
    var sizeInput by remember { mutableStateOf("16") }

    val cm : ClipboardManager = LocalClipboardManager.current

//    var numberInput = rememberTextFieldState()

    fun applyStyleToSelect(style: SpanStyle) {
        val selection = fieldText.selection
        if (!selection.collapsed) {
            val newAnnotatedString = buildAnnotatedString {
                append(fieldText.annotatedString)

                addStyle(
                    style = style,
                    start = selection.start,
                    end = selection.end
                )
            }
            // Update the state
            fieldText = fieldText.copy(annotatedString = newAnnotatedString)
        }
    }

    fun addValAndRebuildStyling(newVal: TextFieldValue) = buildAnnotatedString {
        /* append the new value first, and then for each
        span styles preserved in the oldString,
        add it to the newString. */
        val newText = newVal.annotatedString.text
        val oldString = fieldText.annotatedString
        val oldSelection = fieldText.selection

        append(newText)
        oldString.spanStyles.forEach { styleRange ->
            val start = styleRange.start.coerceAtMost(newText.length)
            val end = styleRange.end.coerceAtMost(newText.length)
            // consider offsetting for changes made before the styling.
            if (start < end) {
                // if the carat/cursor is to the right of the end of the styling.
                if (newVal.selection.start >= end) {
                    addStyle(
                        styleRange.item,
                        start = start,
                        end = end
                    )
                }
                // if the carat/cursor is to the left of the start of the styling range.
                else if (newVal.selection.start < start + 2) {
                    if (end - start != 1) {
                        val diff = newText.length - oldString.length
                        addStyle(
                            styleRange.item,
                            start = (start + diff).coerceAtLeast(0),
                            end = (end + diff).coerceAtLeast(0)
                        )
                    }
                    else {

                    }
                }
                // if the carat/cursor is within the styleRange.
                else {
                    val diff = newText.length - oldString.length
                    addStyle(
                        style = styleRange.item,
                        start = start,
                        end = end + diff
                    )
                }
            }
        }
    }

    Column () {
        TopAppBar(colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            titleContentColor = MaterialTheme.colorScheme.primary,
        ), title={
            Text("Rich text editor")
        }, navigationIcon = {
            IconButton(onClick = onNavigateBack) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Go Back"
                )
            }
        })
        Column(modifier = Modifier.padding(16.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(text = "New note", fontSize = MaterialTheme.typography.displaySmall.fontSize)
                Row() {
                    IconButton(onClick = {
                        fieldText = TextFieldValue(
                            annotatedString = buildAnnotatedString {  }
                        )
                    }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Outlined.NoteAdd,
                            contentDescription = "Add note"
                        )
                    }
                    IconButton(onClick = {}) {
                        Icon(
                            imageVector = Icons.Outlined.Save,
                            contentDescription = "Save note"
                        )
                    }
                }
            }
            BasicTextField(
                value = fieldText,
                onValueChange = { newVal ->
                    // creates a new string for every value change
                    val newString = addValAndRebuildStyling(newVal)
                    fieldText = TextFieldValue(
                        annotatedString = newString,
                        selection = newVal.selection
                    )
                },
                textStyle = TextStyle(
                    color = Color(0xFF11111b),
                    fontSize = 20.sp
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .background(color = Color(0xFFF9E2AF)
                    )
                    .padding(4.dp)

            )
            Row (modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween) {
                Column {
                    Row() {
                        IconButton(onClick = {
                            applyStyleToSelect(SpanStyle(fontWeight = FontWeight.Bold))

                        }) {
                            Icon(
                                imageVector = Icons.Outlined.FormatBold,
                                contentDescription = "Make selected text bold"
                            )
                        }
                        IconButton(onClick = {
                            applyStyleToSelect(SpanStyle(fontStyle = FontStyle.Italic))
                        }) {
                            Icon(
                                imageVector = Icons.Outlined.FormatItalic,
                                contentDescription = "Make selected text italic"
                            )
                        }
                        IconButton(onClick = {
                            applyStyleToSelect(
                                SpanStyle(
                                    fontStyle = FontStyle.Normal,
                                    fontWeight = FontWeight.Normal
                                )
                            )
                        }) {
                            Icon(
                                imageVector = Icons.Outlined.FormatClear,
                                contentDescription = "Normalize"
                            )
                        }
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        OutlinedTextField(
                            value = sizeInput,
                            onValueChange = { newText ->
                                if (newText.all { it.isDigit() }) {
                                    sizeInput = newText
                                }
                            },
                            label = { Text("Size") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.width(64.dp)
                        )
                        OutlinedButton(
                            content = { Text("Change selected") },
                            onClick = {
                                val selection = fieldText.selection
                                val newString = buildAnnotatedString {
                                    append(fieldText.text)
                                    val size = sizeInput.toIntOrNull() ?: 0

                                    fieldText.annotatedString.spanStyles.forEach { styleRange ->
                                        val start =
                                            styleRange.start.coerceAtMost(fieldText.text.length)
                                        val end = styleRange.end.coerceAtMost(fieldText.text.length)
                                        if (start < end) {
                                            addStyle(
                                                styleRange.item,
                                                start = start, end = end
                                            )
                                        }
                                    }

                                    addStyle(
                                        style = SpanStyle(fontSize = size.sp),
                                        start = selection.start,
                                        end = selection.end
                                    )
                                }
                                fieldText = TextFieldValue(
                                    annotatedString = newString,
                                    selection = selection
                                )
                            }
                        )
                    }
                }
                Row() {
                    IconButton(onClick = {
                        val selection = fieldText.selection
                        val start = selection.start
                        val end = selection.end

                        if (!selection.collapsed) {
                            cm.setText(
                                AnnotatedString(
                                    fieldText.annotatedString.substring(start, end)
                                )
                            )
                            val newString = buildAnnotatedString {
                                val new = fieldText.annotatedString.replaceRange(
                                    startIndex = start,
                                    endIndex = end, ""
                                )
                                append(new)
                            }
                            fieldText = TextFieldValue(
                                annotatedString = newString,
                                selection = TextRange(start)
                            )
                        }
                    }) {
                        Icon(
                            imageVector = Icons.Outlined.ContentCut,
                            contentDescription = "Cut selection"
                        )
                    }
                    IconButton(onClick = {
                        val selection = fieldText.selection
                        val start = selection.start
                        val end = selection.end

                        if (!selection.collapsed) {
                            cm.setText(AnnotatedString(fieldText.annotatedString.substring(startIndex = start,
                                endIndex = end)))
                        }

                    }) {
                        Icon(
                            imageVector = Icons.Outlined.ContentCopy,
                            contentDescription = "Copy selection"
                        )
                    }
                    IconButton(onClick = {
                        val selection = fieldText.selection
                        val start = selection.start
                        val end = selection.end
                        val pasteText = cm.getText()

                        if (pasteText != AnnotatedString("") && pasteText != null) {
                            if (selection.collapsed) {
                                val newString = buildAnnotatedString {
                                    val newText = fieldText.text.replaceRange(startIndex = start,
                                        endIndex = end, replacement = pasteText)
                                    append(newText)
                                }
                                val newCursorPos = TextRange(fieldText.selection.start + pasteText.length)
                                fieldText = TextFieldValue(
                                    annotatedString = newString,
                                    selection = newCursorPos
                                )

                            }
                        }
                        }) {
                        Icon(
                            imageVector = Icons.Outlined.ContentPaste,
                            contentDescription = "Paste selection"
                        )
                    }
                }
            }
        }
    }
}

