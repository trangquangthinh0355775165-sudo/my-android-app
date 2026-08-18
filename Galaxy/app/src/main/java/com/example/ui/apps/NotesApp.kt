package com.example.ui.apps

import androidx.compose.animation.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.NoteEntity
import com.example.system.GalaxySystemState
import com.example.system.GalaxyViewModel
import com.example.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

private data class DrawingStroke(
    val points: List<Offset>,
    val color: Color,
    val strokeWidth: Float
)

@Composable
fun NotesApp(
    viewModel: GalaxyViewModel,
    state: GalaxySystemState,
    modifier: Modifier = Modifier
) {
    val notes by viewModel.notes.collectAsState()
    var activeNote by remember { mutableStateOf<NoteEntity?>(null) }
    var isCreatingNew by remember { mutableStateOf(false) }

    // Note Editor Fields
    var noteTitle by remember { mutableStateOf("") }
    var noteContent by remember { mutableStateOf("") }
    var noteCategory by remember { mutableStateOf("Công việc") }
    var isDrawingMode by remember { mutableStateOf(false) }
    var selectedColor by remember { mutableStateOf(Color(0xFF0381FE)) }
    var strokeWidth by remember { mutableStateOf(6f) }
    val strokes = remember { mutableStateListOf<DrawingStroke>() }
    var currentPoints = remember { mutableStateListOf<Offset>() }

    Scaffold(
        modifier = modifier
            .testTag("samsung_notes_app")
            .fillMaxSize(),
        containerColor = if (state.isDarkMode) OneUIDarkBg else OneUILightBg,
        floatingActionButton = {
            if (activeNote == null && !isCreatingNew) {
                FloatingActionButton(
                    onClick = {
                        isCreatingNew = true
                        activeNote = null
                        noteTitle = ""
                        noteContent = ""
                        noteCategory = "Ghi chú"
                        strokes.clear()
                    },
                    containerColor = SamsungBlue,
                    contentColor = Color.White,
                    shape = CircleShape
                ) {
                    Icon(Icons.Rounded.Add, contentDescription = "New Note")
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            if (activeNote == null && !isCreatingNew) {
                // NOTES LIST VIEW
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Header
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Samsung Notes (${notes.size})",
                            color = if (state.isDarkMode) OneUIDarkTextPrimary else OneUILightTextPrimary,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold
                        )

                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            // S-Pen Quick Draw Launch
                            IconButton(
                                onClick = {
                                    isCreatingNew = true
                                    isDrawingMode = true
                                    strokes.clear()
                                    viewModel.toggleSPen()
                                },
                                modifier = Modifier
                                    .size(36.dp)
                                    .background(SamsungOrange.copy(alpha = 0.2f), CircleShape)
                            ) {
                                Icon(
                                    imageVector = Icons.Rounded.Draw,
                                    contentDescription = "S-Pen Canvas",
                                    tint = SamsungOrange,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }

                    if (notes.isEmpty()) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Chưa có ghi chú nào. Nhấn + để tạo!",
                                color = Color.Gray,
                                fontSize = 14.sp
                            )
                        }
                    } else {
                        val dateFmt = remember { SimpleDateFormat("d 'thg' M, HH:mm", Locale.getDefault()) }

                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            items(notes, key = { it.id }) { note ->
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(18.dp))
                                        .clickable {
                                            activeNote = note
                                            noteTitle = note.title
                                            noteContent = note.content
                                            noteCategory = note.category
                                            isDrawingMode = note.isDrawing
                                            strokes.clear()
                                        },
                                    shape = RoundedCornerShape(18.dp),
                                    colors = CardDefaults.cardColors(
                                        containerColor = if (state.isDarkMode) OneUIDarkCard else OneUILightCard
                                    )
                                ) {
                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(14.dp),
                                        verticalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Row(
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                                            ) {
                                                if (note.isDrawing) {
                                                    Icon(
                                                        imageVector = Icons.Rounded.Draw,
                                                        contentDescription = "S-Pen Note",
                                                        tint = SamsungOrange,
                                                        modifier = Modifier.size(16.dp)
                                                    )
                                                }
                                                Text(
                                                    text = note.title,
                                                    color = if (state.isDarkMode) OneUIDarkTextPrimary else OneUILightTextPrimary,
                                                    fontSize = 16.sp,
                                                    fontWeight = FontWeight.SemiBold
                                                )
                                            }

                                            Card(
                                                shape = RoundedCornerShape(8.dp),
                                                colors = CardDefaults.cardColors(
                                                    containerColor = SamsungBlue.copy(alpha = 0.15f)
                                                )
                                            ) {
                                                Text(
                                                    text = note.category,
                                                    color = SamsungBlue,
                                                    fontSize = 10.sp,
                                                    fontWeight = FontWeight.Medium,
                                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                                )
                                            }
                                        }

                                        Text(
                                            text = note.content,
                                            color = if (state.isDarkMode) OneUIDarkTextSecondary else OneUILightTextSecondary,
                                            fontSize = 13.sp,
                                            maxLines = 2,
                                            overflow = TextOverflow.Ellipsis
                                        )

                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(
                                                text = dateFmt.format(Date(note.lastModified)),
                                                color = if (state.isDarkMode) OneUIDarkTextSecondary.copy(alpha = 0.6f) else OneUILightTextSecondary.copy(alpha = 0.6f),
                                                fontSize = 11.sp
                                            )

                                            IconButton(
                                                onClick = { viewModel.deleteNote(note) },
                                                modifier = Modifier.size(24.dp)
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Rounded.DeleteOutline,
                                                    contentDescription = "Delete",
                                                    tint = Color.Gray,
                                                    modifier = Modifier.size(16.dp)
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            } else {
                // NOTE EDITOR / S-PEN DRAWING SCREEN
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 14.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Top Bar
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(onClick = {
                            activeNote = null
                            isCreatingNew = false
                        }) {
                            Icon(
                                imageVector = Icons.Rounded.ArrowBack,
                                contentDescription = "Back",
                                tint = if (state.isDarkMode) Color.White else Color.Black
                            )
                        }

                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            // Mode Switcher (Text vs S-Pen Drawing)
                            FilterChip(
                                selected = !isDrawingMode,
                                onClick = { isDrawingMode = false },
                                label = { Text("Văn bản", fontSize = 11.sp) },
                                leadingIcon = {
                                    Icon(Icons.Rounded.TextFields, contentDescription = null, modifier = Modifier.size(14.dp))
                                }
                            )

                            FilterChip(
                                selected = isDrawingMode,
                                onClick = {
                                    isDrawingMode = true
                                    viewModel.toggleSPen()
                                },
                                label = { Text("Bút S-Pen", fontSize = 11.sp) },
                                leadingIcon = {
                                    Icon(Icons.Rounded.Draw, contentDescription = null, modifier = Modifier.size(14.dp))
                                }
                            )
                        }

                        Button(
                            onClick = {
                                val noteToSave = (activeNote ?: NoteEntity(title = "", content = "")).copy(
                                    title = noteTitle.ifBlank { if (isDrawingMode) "Bản vẽ S-Pen" else "Ghi chú mới" },
                                    content = if (isDrawingMode) "Bản phác thảo bằng bút S-Pen (${strokes.size} nét vẽ)" else noteContent,
                                    category = noteCategory,
                                    isDrawing = isDrawingMode,
                                    lastModified = System.currentTimeMillis()
                                )
                                viewModel.createOrUpdateNote(noteToSave)
                                activeNote = null
                                isCreatingNew = false
                            },
                            shape = RoundedCornerShape(16.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = SamsungBlue),
                            contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp)
                        ) {
                            Text("Lưu", fontSize = 12.sp)
                        }
                    }

                    // Title Input
                    TextField(
                        value = noteTitle,
                        onValueChange = { noteTitle = it },
                        placeholder = { Text("Tiêu đề ghi chú...", fontSize = 16.sp, fontWeight = FontWeight.Bold) },
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    if (!isDrawingMode) {
                        // Text Note Editor
                        TextField(
                            value = noteContent,
                            onValueChange = { noteContent = it },
                            placeholder = { Text("Nhập nội dung ghi chú ở đây...") },
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = Color.Transparent,
                                unfocusedContainerColor = Color.Transparent,
                                focusedIndicatorColor = Color.Transparent,
                                unfocusedIndicatorColor = Color.Transparent
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f)
                        )
                    } else {
                        // Interactive S-Pen Drawing Canvas
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            // Palette & Brush Controls
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(14.dp))
                                    .background(if (state.isDarkMode) OneUIDarkCard else OneUILightCard)
                                    .padding(horizontal = 10.dp, vertical = 6.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                val palette = listOf(
                                    Color(0xFF0381FE),
                                    Color(0xFF2ECC71),
                                    Color(0xFFE74C3C),
                                    Color(0xFFF39C12),
                                    Color(0xFF9B59B6),
                                    if (state.isDarkMode) Color.White else Color.Black
                                )

                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    palette.forEach { color ->
                                        Box(
                                            modifier = Modifier
                                                .size(24.dp)
                                                .clip(CircleShape)
                                                .background(color)
                                                .clickable { selectedColor = color }
                                                .then(
                                                    if (selectedColor == color) Modifier.padding(2.dp) else Modifier
                                                )
                                        )
                                    }
                                }

                                IconButton(
                                    onClick = { strokes.clear() },
                                    modifier = Modifier.size(28.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Rounded.Clear,
                                        contentDescription = "Clear Canvas",
                                        tint = Color.Gray,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }

                            // Interactive Canvas Surface
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(if (state.isDarkMode) Color(0xFF16181F) else Color(0xFFF8FAFD))
                                    .pointerInput(Unit) {
                                        detectDragGestures(
                                            onDragStart = { offset ->
                                                currentPoints.clear()
                                                currentPoints.add(offset)
                                            },
                                            onDrag = { change, _ ->
                                                change.consume()
                                                currentPoints.add(change.position)
                                            },
                                            onDragEnd = {
                                                if (currentPoints.isNotEmpty()) {
                                                    strokes.add(
                                                        DrawingStroke(
                                                            points = currentPoints.toList(),
                                                            color = selectedColor,
                                                            strokeWidth = strokeWidth
                                                        )
                                                    )
                                                    currentPoints.clear()
                                                }
                                            }
                                        )
                                    }
                            ) {
                                Canvas(modifier = Modifier.fillMaxSize()) {
                                    // Draw completed strokes
                                    strokes.forEach { stroke ->
                                        if (stroke.points.size > 1) {
                                            val path = Path().apply {
                                                moveTo(stroke.points.first().x, stroke.points.first().y)
                                                for (i in 1 until stroke.points.size) {
                                                    lineTo(stroke.points[i].x, stroke.points[i].y)
                                                }
                                            }
                                            drawPath(
                                                path = path,
                                                color = stroke.color,
                                                style = Stroke(
                                                    width = stroke.strokeWidth,
                                                    cap = StrokeCap.Round,
                                                    join = StrokeJoin.Round
                                                )
                                            )
                                        }
                                    }

                                    // Draw active stroke
                                    if (currentPoints.size > 1) {
                                        val path = Path().apply {
                                            moveTo(currentPoints.first().x, currentPoints.first().y)
                                            for (i in 1 until currentPoints.size) {
                                                lineTo(currentPoints[i].x, currentPoints[i].y)
                                            }
                                        }
                                        drawPath(
                                            path = path,
                                            color = selectedColor,
                                            style = Stroke(
                                                width = strokeWidth,
                                                cap = StrokeCap.Round,
                                                join = StrokeJoin.Round
                                            )
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
