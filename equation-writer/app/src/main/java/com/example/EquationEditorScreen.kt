package com.example

import android.content.Context
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Brush
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.FormatItalic
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Keyboard
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Title
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Switch
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EquationEditorScreen(
    viewModel: EquationViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current

    // State bindings
    val editorText by viewModel.editorText.collectAsState()
    val latexText by viewModel.latexText.collectAsState()
    val activeStyle by viewModel.activeStyle.collectAsState()
    val bgThemeName by viewModel.bgThemeName.collectAsState()
    val isSerifFont by viewModel.isSerifFont.collectAsState()
    val fontSizeScale by viewModel.fontSizeScale.collectAsState()
    val savedEquations by viewModel.savedEquations.collectAsState()
    val currentEditingId by viewModel.currentEditingId.collectAsState()

    // Screen Tabs: 0 -> Write, 1 -> Presets, 2 -> Saved Library
    var selectedTab by remember { mutableStateOf(0) }

    // Dialog state for saving
    var showSaveDialog by remember { mutableStateOf(false) }
    var saveTitle by remember { mutableStateOf("") }
    var saveCategory by remember { mutableStateOf("") }

    // Symbol Category Selected in custom keyboard
    var selectedKeyboardCategoryIndex by remember { mutableStateOf(0) }

    // Text field state representing current equation
    var textFieldValue by remember { mutableStateOf(TextFieldValue("")) }

    // Whenever editorText is changed from outside (presets or loaded equation), sync textFieldValue
    LaunchedEffect(editorText) {
        if (textFieldValue.text != editorText) {
            textFieldValue = TextFieldValue(
                text = editorText,
                selection = TextRange(editorText.length)
            )
        }
    }

    // Helper to insert symbols at current cursor position
    fun insertSymbol(symbol: String) {
        val currentText = textFieldValue.text
        val selectionStart = textFieldValue.selection.start
        val selectionEnd = textFieldValue.selection.end
        
        val newText = StringBuilder(currentText)
            .replace(selectionStart, selectionEnd, symbol)
            .toString()
            
        val newCursorPos = selectionStart + symbol.length
        textFieldValue = TextFieldValue(
            text = newText,
            selection = TextRange(newCursorPos)
        )
        viewModel.updateEditorText(newText)
    }

    // Helper to delete character or selection at current cursor position
    fun deleteLastCharacter() {
        val currentText = textFieldValue.text
        val selectionStart = textFieldValue.selection.start
        val selectionEnd = textFieldValue.selection.end
        
        if (selectionStart != selectionEnd) {
            val newText = StringBuilder(currentText)
                .delete(selectionStart, selectionEnd)
                .toString()
            textFieldValue = TextFieldValue(
                text = newText,
                selection = TextRange(selectionStart)
            )
            viewModel.updateEditorText(newText)
        } else if (selectionStart > 0) {
            val newText = StringBuilder(currentText)
                .deleteAt(selectionStart - 1)
                .toString()
            textFieldValue = TextFieldValue(
                text = newText,
                selection = TextRange(selectionStart - 1)
            )
            viewModel.updateEditorText(newText)
        }
    }

    // Map active theme
    val activeTheme = remember(bgThemeName) {
        EquationTheme.values().find { it.name == bgThemeName } ?: EquationTheme.CHALKBOARD
    }

    // Render screen
    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = Color(0xFF1C1B1F),
        topBar = {
            Column(
                modifier = Modifier
                    .background(Color(0xFF1C1B1F))
                    .statusBarsPadding()
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Equation Writer",
                            style = TextStyle(
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Serif,
                                color = Color(0xFFE6E1E5)
                            )
                        )
                        Text(
                            text = if (currentEditingId != null) "Editing Saved Equation" else "Create beautifully styled mathematical formulas",
                            style = TextStyle(
                                fontSize = 12.sp,
                                color = Color(0xFFCAC4D0)
                            )
                        )
                    }

                    Row {
                        if (editorText.isNotEmpty()) {
                            IconButton(
                                onClick = {
                                    viewModel.clearEditor()
                                    textFieldValue = TextFieldValue("")
                                    Toast.makeText(context, "Editor cleared", Toast.LENGTH_SHORT).show()
                                },
                                modifier = Modifier.testTag("clear_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Clear,
                                    contentDescription = "Clear Editor",
                                    tint = MaterialTheme.colorScheme.error
                                )
                            }
                        }

                        IconButton(
                            onClick = {
                                if (editorText.isBlank()) {
                                    Toast.makeText(context, "Write an equation before saving", Toast.LENGTH_SHORT).show()
                                } else {
                                    saveTitle = if (currentEditingId != null) {
                                        savedEquations.find { it.id == currentEditingId }?.title ?: ""
                                    } else {
                                        ""
                                    }
                                    saveCategory = if (currentEditingId != null) {
                                        savedEquations.find { it.id == currentEditingId }?.category ?: "Algebra"
                                    } else {
                                        "Algebra"
                                    }
                                    showSaveDialog = true
                                }
                            },
                            modifier = Modifier.testTag("save_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Save,
                                contentDescription = "Save Equation",
                                tint = Color(0xFFD0BCFF)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Modern visual TabRow
                TabRow(
                    selectedTabIndex = selectedTab,
                    containerColor = Color.Transparent,
                    contentColor = Color(0xFFE6E1E5),
                    indicator = { tabPositions ->
                        TabRowDefaults.SecondaryIndicator(
                            modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                            color = Color(0xFFD0BCFF),
                            height = 3.dp
                        )
                    }
                ) {
                    Tab(
                        selected = selectedTab == 0,
                        onClick = { selectedTab = 0 },
                        selectedContentColor = Color(0xFFD0BCFF),
                        unselectedContentColor = Color(0xFFCAC4D0),
                        text = { Text("Write", fontWeight = FontWeight.Medium) }
                    )
                    Tab(
                        selected = selectedTab == 1,
                        onClick = { selectedTab = 1 },
                        selectedContentColor = Color(0xFFD0BCFF),
                        unselectedContentColor = Color(0xFFCAC4D0),
                        text = { Text("Presets", fontWeight = FontWeight.Medium) }
                    )
                    Tab(
                        selected = selectedTab == 2,
                        onClick = { selectedTab = 2 },
                        selectedContentColor = Color(0xFFD0BCFF),
                        unselectedContentColor = Color(0xFFCAC4D0),
                        text = { Text("Library (${savedEquations.size})", fontWeight = FontWeight.Medium) }
                    )
                }
            }
        },
        contentWindowInsets = WindowInsets.navigationBars
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (selectedTab) {
                0 -> {
                    // WRITE TAB
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                    ) {
                        // 1. EQUATION PREVIEW CARD
                        EquationPreviewCard(
                            text = editorText.ifEmpty { "𝒻(𝓍) = 𝒂 𝓍² + 𝒃 𝓍 + 𝒄" },
                            theme = activeTheme,
                            isSerif = isSerifFont,
                            scale = fontSizeScale,
                            latex = latexText,
                            onCopyUnicode = {
                                clipboardManager.setText(AnnotatedString(editorText))
                                Toast.makeText(context, "Unicode copied to clipboard!", Toast.LENGTH_SHORT).show()
                            },
                            onCopyLatex = {
                                val code = latexText.ifEmpty { editorText }
                                clipboardManager.setText(AnnotatedString(code))
                                Toast.makeText(context, "LaTeX copied to clipboard!", Toast.LENGTH_SHORT).show()
                            },
                            onShare = {
                                val shareIntent = android.content.Intent().apply {
                                    action = android.content.Intent.ACTION_SEND
                                    putExtra(
                                        android.content.Intent.EXTRA_TEXT,
                                        "Equation: $editorText\nLaTeX: ${latexText.ifEmpty { "N/A" }}"
                                    )
                                    type = "text/plain"
                                }
                                context.startActivity(android.content.Intent.createChooser(shareIntent, "Share Equation"))
                            },
                            onExportPng = {
                                if (editorText.isBlank()) {
                                    Toast.makeText(context, "Write an equation first!", Toast.LENGTH_SHORT).show()
                                } else {
                                    val uri = EquationExporter.exportToPng(context, editorText, activeTheme, isSerifFont, fontSizeScale)
                                    if (uri != null) {
                                        val shareIntent = android.content.Intent().apply {
                                            action = android.content.Intent.ACTION_SEND
                                            putExtra(android.content.Intent.EXTRA_STREAM, uri)
                                            type = "image/png"
                                            addFlags(android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION)
                                        }
                                        context.startActivity(android.content.Intent.createChooser(shareIntent, "Save or Share PNG Card"))
                                    } else {
                                        Toast.makeText(context, "Failed to render PNG", Toast.LENGTH_SHORT).show()
                                    }
                                }
                            },
                            onExportSvg = {
                                if (editorText.isBlank()) {
                                    Toast.makeText(context, "Write an equation first!", Toast.LENGTH_SHORT).show()
                                } else {
                                    val svgContent = EquationExporter.generateSvg(editorText, activeTheme, isSerifFont, fontSizeScale)
                                    val uri = EquationExporter.exportToSvg(context, svgContent)
                                    if (uri != null) {
                                        val shareIntent = android.content.Intent().apply {
                                            action = android.content.Intent.ACTION_SEND
                                            putExtra(android.content.Intent.EXTRA_STREAM, uri)
                                            type = "image/svg+xml"
                                            addFlags(android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION)
                                        }
                                        context.startActivity(android.content.Intent.createChooser(shareIntent, "Save or Share SVG Vector"))
                                    } else {
                                        Toast.makeText(context, "Failed to export SVG", Toast.LENGTH_SHORT).show()
                                    }
                                }
                            },
                            onExportTex = {
                                val code = latexText.ifEmpty { editorText }
                                if (code.isBlank()) {
                                    Toast.makeText(context, "Write an equation first!", Toast.LENGTH_SHORT).show()
                                } else {
                                    val uri = EquationExporter.exportToTex(context, code)
                                    if (uri != null) {
                                        val shareIntent = android.content.Intent().apply {
                                            action = android.content.Intent.ACTION_SEND
                                            putExtra(android.content.Intent.EXTRA_STREAM, uri)
                                            type = "text/plain"
                                            addFlags(android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION)
                                        }
                                        context.startActivity(android.content.Intent.createChooser(shareIntent, "Save or Share LaTeX .tex File"))
                                    } else {
                                        Toast.makeText(context, "Failed to export .tex file", Toast.LENGTH_SHORT).show()
                                    }
                                }
                            }
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // 2. RENDERING PREFERENCES ROW
                        Text(
                            text = "Card Customizer",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFFCAC4D0)
                        )
                        
                        Spacer(modifier = Modifier.height(8.dp))

                        Card(
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = Color(0xFF25232A)
                            ),
                            border = BorderStroke(1.dp, Color(0xFF49454F))
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                // Theme selector
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Palette,
                                        contentDescription = null,
                                        modifier = Modifier.size(20.dp),
                                        tint = Color(0xFFD0BCFF)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Theme: ", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium, color = Color(0xFFE6E1E5))
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .horizontalScroll(rememberScrollState()),
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        EquationTheme.values().forEach { th ->
                                            Box(
                                                modifier = Modifier
                                                    .size(28.dp)
                                                    .clip(CircleShape)
                                                    .background(Brush.linearGradient(th.bgGradient))
                                                    .border(
                                                        width = if (bgThemeName == th.name) 2.dp else 1.dp,
                                                        color = if (bgThemeName == th.name) Color(0xFFD0BCFF) else Color.Gray.copy(alpha = 0.5f),
                                                        shape = CircleShape
                                                    )
                                                    .clickable { viewModel.setTheme(th.name) }
                                            )
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(12.dp))

                                // Font scale slider & Serif Toggle
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.Default.FormatItalic,
                                            contentDescription = null,
                                            modifier = Modifier.size(20.dp),
                                            tint = Color(0xFFD0BCFF)
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text("Mathematical Serif Font", style = MaterialTheme.typography.bodyMedium, color = Color(0xFFE6E1E5))
                                    }
                                    Switch(
                                        checked = isSerifFont,
                                        onCheckedChange = { viewModel.toggleFontSerif() }
                                    )
                                }

                                Spacer(modifier = Modifier.height(8.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text("Font Size", style = MaterialTheme.typography.bodyMedium, color = Color(0xFFE6E1E5), modifier = Modifier.width(72.dp))
                                    Slider(
                                        value = fontSizeScale,
                                        onValueChange = { viewModel.setFontSizeScale(it) },
                                        valueRange = 0.7f..1.5f,
                                        modifier = Modifier.weight(1f)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // 3. EQUATION INPUT FIELD
                        Text(
                            text = "Equation Input",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFFCAC4D0)
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        OutlinedTextField(
                            value = textFieldValue,
                            onValueChange = { newValue ->
                                val oldVal = textFieldValue
                                val convertedValue = handleTextChange(oldVal, newValue, activeStyle)
                                textFieldValue = convertedValue
                                viewModel.updateEditorText(convertedValue.text)
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("equation_input"),
                            placeholder = { Text("Type here or use symbols keyboard below") },
                            textStyle = TextStyle(
                                fontSize = 18.sp,
                                fontFamily = if (isSerifFont) FontFamily.Serif else FontFamily.Default,
                                color = Color(0xFFE6E1E5)
                            ),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color(0xFFD0BCFF),
                                unfocusedBorderColor = Color(0xFF49454F),
                                focusedContainerColor = Color(0xFF25232A),
                                unfocusedContainerColor = Color(0xFF25232A),
                                focusedLabelColor = Color(0xFFD0BCFF),
                                unfocusedLabelColor = Color(0xFFCAC4D0)
                            ),
                            shape = RoundedCornerShape(16.dp)
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        // Optional LaTeX entry accompanying the editor
                        OutlinedTextField(
                            value = latexText,
                            onValueChange = { viewModel.updateLatexText(it) },
                            modifier = Modifier.fillMaxWidth(),
                            placeholder = { Text("Optional: Enter LaTeX code (e.g. \\int x^2 dx)") },
                            label = { Text("LaTeX Output / Input") },
                            textStyle = TextStyle(fontSize = 14.sp, fontFamily = FontFamily.Monospace, color = Color(0xFFE6E1E5)),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color(0xFFD0BCFF),
                                unfocusedBorderColor = Color(0xFF49454F),
                                focusedContainerColor = Color(0xFF25232A),
                                unfocusedContainerColor = Color(0xFF25232A),
                                focusedLabelColor = Color(0xFFD0BCFF),
                                unfocusedLabelColor = Color(0xFFCAC4D0)
                            ),
                            singleLine = true,
                            shape = RoundedCornerShape(16.dp)
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // 4. ACTIVE MATH STYLE SELECTION
                        Text(
                            text = "Active Typing Font Style",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFFCAC4D0)
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            MathStyle.values().forEach { style ->
                                val isSelected = activeStyle == style
                                FilterChip(
                                    selected = isSelected,
                                    onClick = { viewModel.setStyle(style) },
                                    label = { Text(style.displayName) },
                                    leadingIcon = {
                                        if (isSelected) {
                                            Icon(
                                                imageVector = Icons.Default.Edit,
                                                contentDescription = null,
                                                modifier = Modifier.size(16.dp)
                                            )
                                        }
                                    },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = Color(0xFF4A4458),
                                        selectedLabelColor = Color(0xFFE6E1E5),
                                        containerColor = Color(0xFF25232A),
                                        labelColor = Color(0xFFCAC4D0)
                                    )
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // 5. MATHEMATICAL SYMBOLS CUSTOM KEYBOARD
                        Text(
                            text = "Symbol Board",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFFCAC4D0)
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        Card(
                            shape = RoundedCornerShape(28.dp),
                            border = BorderStroke(1.dp, Color(0xFF49454F)),
                            colors = CardDefaults.cardColors(
                                containerColor = Color(0xFF2B2930)
                            )
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                // Keyboard category selectors (Tab-like row)
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(Color(0xFF2B2930))
                                        .horizontalScroll(rememberScrollState())
                                        .padding(vertical = 4.dp, horizontal = 4.dp),
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    MathSymbols.categories.forEachIndexed { idx, category ->
                                        val isCatSelected = selectedKeyboardCategoryIndex == idx
                                        val chipBgColor = animateColorAsState(
                                            if (isCatSelected) Color(0xFF4A4458) else Color.Transparent
                                        )
                                        val chipTextColor = animateColorAsState(
                                            if (isCatSelected) Color(0xFFD0BCFF) else Color(0xFFCAC4D0)
                                        )

                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(12.dp))
                                                .background(chipBgColor.value)
                                                .border(
                                                    width = 1.dp,
                                                    color = if (isCatSelected) Color(0xFF49454F) else Color.Transparent,
                                                    shape = RoundedCornerShape(12.dp)
                                                )
                                                .clickable { selectedKeyboardCategoryIndex = idx }
                                                .padding(horizontal = 12.dp, vertical = 8.dp),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Text(category.icon, fontSize = 14.sp)
                                                Spacer(modifier = Modifier.width(6.dp))
                                                Text(
                                                    text = category.name,
                                                    color = chipTextColor.value,
                                                    fontSize = 13.sp,
                                                    fontWeight = FontWeight.Medium
                                                )
                                            }
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(10.dp))

                                // Symbol grid
                                val activeCategory = MathSymbols.categories[selectedKeyboardCategoryIndex]
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(180.dp)
                                ) {
                                    LazyVerticalGrid(
                                        columns = GridCells.Adaptive(minSize = 44.dp),
                                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                                        verticalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        items(activeCategory.symbols) { symbol ->
                                            Box(
                                                modifier = Modifier
                                                    .size(44.dp)
                                                    .clip(RoundedCornerShape(12.dp))
                                                    .background(Color(0xFF36343B))
                                                    .border(1.dp, Color(0xFF49454F), RoundedCornerShape(12.dp))
                                                    .clickable { insertSymbol(symbol) },
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Text(
                                                    text = symbol,
                                                    fontSize = 18.sp,
                                                    fontWeight = FontWeight.Medium,
                                                    color = Color(0xFFE6E1E5),
                                                    fontFamily = if (isSerifFont) FontFamily.Serif else FontFamily.Default
                                                )
                                            }
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(12.dp))

                                // Bottom Action Row
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    // Backspace key
                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .height(48.dp)
                                            .clip(RoundedCornerShape(16.dp))
                                            .background(Color(0xFFEFB8C8))
                                            .clickable { deleteLastCharacter() },
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Row(
                                            horizontalArrangement = Arrangement.Center,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Clear,
                                                contentDescription = "Backspace",
                                                tint = Color(0xFF492532),
                                                modifier = Modifier.size(18.dp)
                                            )
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text(
                                                text = "Delete",
                                                color = Color(0xFF492532),
                                                fontSize = 14.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                    }

                                    // Return/Save key
                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .height(48.dp)
                                            .clip(RoundedCornerShape(16.dp))
                                            .background(Color(0xFFD0BCFF))
                                            .clickable {
                                                if (editorText.isBlank()) {
                                                    Toast.makeText(context, "Write an equation before saving", Toast.LENGTH_SHORT).show()
                                                } else {
                                                    saveTitle = if (currentEditingId != null) {
                                                        savedEquations.find { it.id == currentEditingId }?.title ?: ""
                                                    } else {
                                                        ""
                                                    }
                                                    saveCategory = if (currentEditingId != null) {
                                                        savedEquations.find { it.id == currentEditingId }?.category ?: "Algebra"
                                                    } else {
                                                        "Algebra"
                                                    }
                                                    showSaveDialog = true
                                                }
                                            },
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Row(
                                            horizontalArrangement = Arrangement.Center,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Save,
                                                contentDescription = "Save",
                                                tint = Color(0xFF381E72),
                                                modifier = Modifier.size(18.dp)
                                            )
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text(
                                                text = "Save",
                                                color = Color(0xFF381E72),
                                                fontSize = 14.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                    }
                                }
                            }
                        }
                        
                        Spacer(modifier = Modifier.height(24.dp))
                    }
                }

                1 -> {
                    // PRESETS TAB
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        item {
                            Text(
                                text = "Standard Equations",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFD0BCFF)
                            )
                            Text(
                                text = "Tap any standard equation preset to load it instantly into the workspace for editing or study.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color(0xFFCAC4D0)
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                        }

                        items(MathSymbols.presets) { preset ->
                            PresetItemCard(
                                preset = preset,
                                onLoad = {
                                    viewModel.loadPreset(preset)
                                    selectedTab = 0 // Switch back to editor
                                    Toast.makeText(context, "Loaded: ${preset.title}", Toast.LENGTH_SHORT).show()
                                }
                            )
                        }
                    }
                }

                2 -> {
                    // LIBRARY TAB
                    if (savedEquations.isEmpty()) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(32.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Book,
                                    contentDescription = null,
                                    modifier = Modifier.size(64.dp),
                                    tint = Color(0xFFD0BCFF).copy(alpha = 0.4f)
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                                Text(
                                    text = "Your library is empty",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFFE6E1E5)
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "Equations you save in the editor will appear here. Build something mathematical!",
                                    style = MaterialTheme.typography.bodyMedium,
                                    textAlign = TextAlign.Center,
                                    color = Color(0xFFCAC4D0)
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                                Button(
                                    onClick = { selectedTab = 0 },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = Color(0xFFD0BCFF),
                                        contentColor = Color(0xFF381E72)
                                    )
                                ) {
                                    Text("Open Editor")
                                }
                            }
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            item {
                                Text(
                                    text = "Saved Formulas",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFFD0BCFF)
                                )
                                Text(
                                    text = "Tap to load and edit, or use quick actions to share/delete your creations.",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = Color(0xFFCAC4D0)
                                )
                                Spacer(modifier = Modifier.height(12.dp))
                            }

                            items(savedEquations) { equation ->
                                SavedEquationItemCard(
                                    equation = equation,
                                    onLoad = {
                                        viewModel.loadEquation(equation)
                                        selectedTab = 0 // Switch back to editor
                                        Toast.makeText(context, "Loaded saved equation", Toast.LENGTH_SHORT).show()
                                    },
                                    onDelete = {
                                        viewModel.deleteEquation(equation)
                                        Toast.makeText(context, "Deleted equation", Toast.LENGTH_SHORT).show()
                                    },
                                    onShare = {
                                        val shareIntent = android.content.Intent().apply {
                                            action = android.content.Intent.ACTION_SEND
                                            putExtra(
                                                android.content.Intent.EXTRA_TEXT,
                                                "Equation: ${equation.unicodeContent}\nLaTeX: ${equation.latexContent.ifEmpty { "N/A" }}"
                                            )
                                            type = "text/plain"
                                        }
                                        context.startActivity(android.content.Intent.createChooser(shareIntent, "Share Equation"))
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    // Save Equation dialog
    if (showSaveDialog) {
        AlertDialog(
            onDismissRequest = { showSaveDialog = false },
            title = { Text(if (currentEditingId != null) "Update Saved Equation" else "Save Equation") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(
                        text = "Set a title and a category label to keep your mathematical workspace organized.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    OutlinedTextField(
                        value = saveTitle,
                        onValueChange = { saveTitle = it },
                        label = { Text("Title") },
                        placeholder = { Text("e.g. Schrödinger Equation") },
                        singleLine = true,
                        leadingIcon = { Icon(imageVector = Icons.Default.Title, contentDescription = null) },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = saveCategory,
                        onValueChange = { saveCategory = it },
                        label = { Text("Category") },
                        placeholder = { Text("e.g. Physics, Calculus, Algebra") },
                        singleLine = true,
                        leadingIcon = { Icon(imageVector = Icons.Default.Palette, contentDescription = null) },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.saveCurrentEquation(saveTitle, saveCategory)
                        showSaveDialog = false
                        Toast.makeText(context, "Equation saved successfully!", Toast.LENGTH_SHORT).show()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Text("Save")
                }
            },
            dismissButton = {
                TextButton(onClick = { showSaveDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

// 1. EQUATION PREVIEW CARD
@Composable
fun EquationPreviewCard(
    text: String,
    theme: EquationTheme,
    isSerif: Boolean,
    scale: Float,
    latex: String,
    onCopyUnicode: () -> Unit,
    onCopyLatex: () -> Unit,
    onShare: () -> Unit,
    onExportPng: () -> Unit,
    onExportSvg: () -> Unit,
    onExportTex: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("preview_card"),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Brush.verticalGradient(theme.bgGradient))
                .padding(20.dp)
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                // Card header / style tag
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        color = theme.accentColor.copy(alpha = 0.2f),
                        shape = RoundedCornerShape(8.dp),
                        border = BorderStroke(1.dp, theme.accentColor.copy(alpha = 0.4f))
                    ) {
                        Text(
                            text = theme.displayName,
                            color = theme.accentColor,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }

                    Row {
                        IconButton(onClick = onCopyUnicode, modifier = Modifier.size(32.dp)) {
                            Icon(
                                imageVector = Icons.Default.ContentCopy,
                                contentDescription = "Copy Unicode",
                                tint = theme.textColor.copy(alpha = 0.8f),
                                modifier = Modifier.size(16.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        IconButton(onClick = onShare, modifier = Modifier.size(32.dp)) {
                            Icon(
                                imageVector = Icons.Default.Share,
                                contentDescription = "Share",
                                tint = theme.textColor.copy(alpha = 0.8f),
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // The equation text itself in large mathematical font!
                Text(
                    text = text,
                    style = TextStyle(
                        fontSize = (26 * scale).sp,
                        fontWeight = FontWeight.Medium,
                        fontFamily = if (isSerif) FontFamily.Serif else FontFamily.Default,
                        color = theme.textColor,
                        textAlign = TextAlign.Center
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 16.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Small formula footer showing LaTeX equivalent if available
                if (latex.isNotEmpty()) {
                    Text(
                        text = "latex: $latex",
                        style = TextStyle(
                            fontSize = 11.sp,
                            fontFamily = FontFamily.Monospace,
                            color = theme.textColor.copy(alpha = 0.6f)
                        ),
                        maxLines = 1,
                        modifier = Modifier
                            .clickable { onCopyLatex() }
                            .background(
                                color = theme.textColor.copy(alpha = 0.05f),
                                shape = RoundedCornerShape(4.dp)
                            )
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Export Action Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Export PNG Button
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(theme.textColor.copy(alpha = 0.12f))
                            .clickable { onExportPng() }
                            .padding(horizontal = 14.dp, vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Image,
                                contentDescription = "Export PNG",
                                tint = theme.textColor.copy(alpha = 0.9f),
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "PNG Image",
                                color = theme.textColor.copy(alpha = 0.9f),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    // Export SVG Button
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(theme.textColor.copy(alpha = 0.12f))
                            .clickable { onExportSvg() }
                            .padding(horizontal = 14.dp, vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Code,
                                contentDescription = "Export SVG",
                                tint = theme.textColor.copy(alpha = 0.9f),
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "SVG Vector",
                                color = theme.textColor.copy(alpha = 0.9f),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    // Export .tex Button
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(theme.textColor.copy(alpha = 0.12f))
                            .clickable { onExportTex() }
                            .padding(horizontal = 14.dp, vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Description,
                                contentDescription = "Export LaTeX File",
                                tint = theme.textColor.copy(alpha = 0.9f),
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = ".TEX LaTeX",
                                color = theme.textColor.copy(alpha = 0.9f),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }
}

// 2. PRESET ITEM CARD
@Composable
fun PresetItemCard(
    preset: EquationPreset,
    onLoad: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
        ),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = preset.title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = preset.category,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Button(
                    onClick = onLoad,
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Text("Load", fontSize = 12.sp)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = preset.description,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Formula Preview inside Preset
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(8.dp))
                    .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(8.dp))
                    .padding(12.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = preset.unicode,
                    style = TextStyle(
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Medium,
                        fontFamily = FontFamily.Serif,
                        color = MaterialTheme.colorScheme.onSurface,
                        textAlign = TextAlign.Center
                    )
                )
            }
        }
    }
}

// 3. SAVED EQUATION ITEM CARD
@Composable
fun SavedEquationItemCard(
    equation: Equation,
    onLoad: () -> Unit,
    onDelete: () -> Unit,
    onShare: () -> Unit
) {
    val theme = remember(equation.bgThemeName) {
        EquationTheme.values().find { it.name == equation.bgThemeName } ?: EquationTheme.CHALKBOARD
    }
    val formattedDate = remember(equation.timestamp) {
        val sdf = SimpleDateFormat("MMM dd, yyyy - HH:mm", Locale.getDefault())
        sdf.format(Date(equation.timestamp))
    }

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f)
        ),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = equation.title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = equation.category,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "•  $formattedDate",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                        )
                    }
                }

                Row {
                    IconButton(onClick = onLoad) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Edit Saved Equation",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                    IconButton(onClick = onShare) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = "Share Saved Equation",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    IconButton(onClick = onDelete) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Delete Saved Equation",
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Mini Equation Preview styled card
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(90.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(Brush.verticalGradient(theme.bgGradient))
                    .padding(12.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = equation.unicodeContent,
                    style = TextStyle(
                        fontSize = (18 * equation.fontSizeScale).sp,
                        fontWeight = FontWeight.Medium,
                        fontFamily = if (equation.isSerifFont) FontFamily.Serif else FontFamily.Default,
                        color = theme.textColor,
                        textAlign = TextAlign.Center
                    ),
                    maxLines = 2
                )
            }
        }
    }
}

// Interceptor function to replace text typed with the appropriate Math Style
fun handleTextChange(
    oldValue: TextFieldValue,
    newValue: TextFieldValue,
    style: MathStyle
): TextFieldValue {
    if (newValue.text.length <= oldValue.text.length) {
        // Deletion, paste replacement, or clear, accept as-is
        return newValue
    }

    val oldText = oldValue.text
    val newText = newValue.text

    // If change is typing single/multiple characters at the end of selection/cursor
    val cursorIndex = oldValue.selection.end
    val addedLength = newText.length - oldText.length

    if (cursorIndex >= 0 && cursorIndex <= oldText.length) {
        val addedText = newText.substring(cursorIndex, cursorIndex + addedLength)
        val styledAddedText = MathFontConverter.convertString(addedText, style)

        val updatedText = StringBuilder(oldText)
            .insert(cursorIndex, styledAddedText)
            .toString()

        val newSelectionStart = cursorIndex + styledAddedText.length
        return TextFieldValue(
            text = updatedText,
            selection = TextRange(newSelectionStart)
        )
    }

    return newValue
}

enum class EquationTheme(
    val displayName: String,
    val bgGradient: List<Color>,
    val textColor: Color,
    val accentColor: Color,
    val isDark: Boolean
) {
    ELEGANT_DARK(
        "Elegant Dark",
        listOf(Color(0xFF25232A), Color(0xFF1C1B1F)),
        Color(0xFFE6E1E5),
        Color(0xFFD0BCFF),
        isDark = true
    ),
    CHALKBOARD(
        "Chalkboard",
        listOf(Color(0xFF1B3124), Color(0xFF0F1E15)),
        Color(0xFFE8F1EB),
        Color(0xFF86C29B),
        isDark = true
    ),
    DEEP_SPACE(
        "Deep Space",
        listOf(Color(0xFF0B0E17), Color(0xFF1F1A3A)),
        Color(0xFFE3E8F8),
        Color(0xFF819FF7),
        isDark = true
    ),
    PARCHMENT(
        "Parchment",
        listOf(Color(0xFFF6F0DD), Color(0xFFECE1BE)),
        Color(0xFF2C2415),
        Color(0xFF8C7139),
        isDark = false
    ),
    NEON_EMERALD(
        "Quantum",
        listOf(Color(0xFF101412), Color(0xFF1A221E)),
        Color(0xFF2ECC71),
        Color(0xFF27AE60),
        isDark = true
    ),
    SUNSET_MESH(
        "Sunset",
        listOf(Color(0xFFD35400), Color(0xFFC0392B)),
        Color(0xFFFFFFFF),
        Color(0xFFF1C40F),
        isDark = true
    ),
    ACADEMIC_WHITE(
        "Academic",
        listOf(Color(0xFFFFFFFF), Color(0xFFF2F4F4)),
        Color(0xFF1C2833),
        Color(0xFF34495E),
        isDark = false
    )
}
