package com.cavacollective.sorrydeck

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// --- Sorry!-inspired palette (no trademarks) ---
private val FeltGreen = Color(0xFF1B5E20)
private val CardCream = Color(0xFFFFFDF6)
private val CardInk = Color(0xFF1C1C1C)
private val SorryRed = Color(0xFFC62828)
private val AccentBlue = Color(0xFF1565C0)
private val AccentYellow = Color(0xFFF9A825)
private val CardBackRed = Color(0xFFB71C1C)
private val CardBackTrim = Color(0xFFFFD54F)

// Flip animation length (ms)
private const val FLIP_DURATION_MS = 900

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = FeltGreen
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .windowInsetsPadding(WindowInsets.systemBars)
                ) {
                    DeckScreen()
                }
            }
        }
    }
}

@Composable
fun DeckScreen() {
    val context = LocalContext.current
    val soundManager = remember { SoundManager(context) }
    DisposableEffect(Unit) {
        onDispose { soundManager.release() }
    }

    var deck by remember { mutableStateOf(Deck.newShuffled()) }
    var revealedCount by rememberSaveable { mutableIntStateOf(0) }
    var viewIndex by rememberSaveable { mutableIntStateOf(-1) }
    var showReshuffleConfirm by remember { mutableStateOf(false) }

    val totalCards = deck.size
    val deckEmpty = revealedCount >= totalCards
    val viewingHistory by remember {
        derivedStateOf { viewIndex in 0 until (revealedCount - 1) }
    }
    val currentCard: Card? = if (viewIndex in deck.indices) deck[viewIndex] else null

    fun drawNext() {
        if (deckEmpty) return
        revealedCount += 1
        viewIndex = revealedCount - 1
        val drawn = deck[viewIndex]
        soundManager.playForCard(drawn)
    }

    fun reshuffle() {
        deck = Deck.newShuffled()
        revealedCount = 0
        viewIndex = -1
        soundManager.playFlip()
    }

    fun goBack() {
        if (viewIndex > 0) {
            viewIndex -= 1
            soundManager.playFlip()
        }
    }

    fun goForward() {
        if (viewIndex < revealedCount - 1) {
            viewIndex += 1
            soundManager.playFlip()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp, vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Header row: title centered, reshuffle button top-right
        Box(modifier = Modifier.fillMaxWidth()) {
            Column(
                modifier = Modifier.align(Alignment.Center),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "SORRY DECK",
                    color = CardBackTrim,
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 4.sp
                )
                Spacer(Modifier.height(4.dp))
                val counterText = when {
                    revealedCount == 0 -> "$totalCards cards · tap to draw"
                    viewingHistory -> "Reviewing card ${viewIndex + 1} of $revealedCount drawn"
                    else -> "Card $revealedCount of $totalCards"
                }
                Text(
                    text = counterText,
                    color = Color.White.copy(alpha = 0.85f),
                    fontSize = 14.sp
                )
            }

            // Mid-game reshuffle icon (top-right). Hidden when deck is empty
            // because the big SHUFFLE button below takes over.
            if (!deckEmpty && revealedCount > 0) {
                IconButton(
                    onClick = { showReshuffleConfirm = true },
                    modifier = Modifier.align(Alignment.CenterEnd)
                ) {
                    Icon(
                        Icons.Default.Refresh,
                        contentDescription = "Reshuffle deck",
                        tint = CardBackTrim,
                        modifier = Modifier.size(28.dp)
                    )
                }
            }
        }

        Spacer(Modifier.height(20.dp))

        // Card area
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            FlipCard(
                card = currentCard,
                deckEmpty = deckEmpty,
                onTap = {
                    if (!deckEmpty && !viewingHistory) drawNext()
                }
            )
        }

        Spacer(Modifier.height(20.dp))

        // Bottom controls: back / center action / forward
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = { goBack() },
                enabled = viewIndex > 0
            ) {
                Icon(
                    Icons.Default.ChevronLeft,
                    contentDescription = "Previous card",
                    tint = if (viewIndex > 0) Color.White else Color.White.copy(alpha = 0.3f),
                    modifier = Modifier.size(40.dp)
                )
            }

            if (deckEmpty) {
                Button(
                    onClick = { reshuffle() },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = CardBackTrim,
                        contentColor = CardInk
                    )
                ) {
                    Icon(Icons.Default.Refresh, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text("SHUFFLE", fontWeight = FontWeight.Bold, letterSpacing = 2.sp)
                }
            } else {
                Text(
                    text = if (viewingHistory) "Tap → to return" else "Tap card to draw",
                    color = Color.White.copy(alpha = 0.7f),
                    fontSize = 13.sp
                )
            }

            IconButton(
                onClick = { goForward() },
                enabled = viewIndex < revealedCount - 1
            ) {
                Icon(
                    Icons.Default.ChevronRight,
                    contentDescription = "Next reviewed card",
                    tint = if (viewIndex < revealedCount - 1) Color.White else Color.White.copy(alpha = 0.3f),
                    modifier = Modifier.size(40.dp)
                )
            }
        }
    }

    // Reshuffle confirmation dialog
    if (showReshuffleConfirm) {
        AlertDialog(
            onDismissRequest = { showReshuffleConfirm = false },
            title = { Text("Reshuffle deck?") },
            text = {
                Text(
                    "This will shuffle all 45 cards back together and reset the count. " +
                            "You'll lose your current draw history."
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showReshuffleConfirm = false
                        reshuffle()
                    }
                ) {
                    Text("RESHUFFLE", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showReshuffleConfirm = false }) {
                    Text("CANCEL")
                }
            }
        )
    }
}

/**
 * Animates the card flipping in from -90° to 0° on Y-axis whenever the
 * displayed card changes. We swap which face is drawn at the midpoint so
 * the back is visible during the first half and the front during the second.
 */
@Composable
fun FlipCard(
    card: Card?,
    deckEmpty: Boolean,
    onTap: () -> Unit
) {
    // A monotonically-increasing key triggers a new flip animation each time
    // the displayed card changes.
    val flipKey = remember(card, deckEmpty) {
        when {
            deckEmpty -> Int.MAX_VALUE
            card == null -> -1
            else -> System.nanoTime().toInt()
        }
    }

    // Drive a 0f -> 1f progress for each flip
    var animTarget by remember { mutableStateOf(0f) }
    LaunchedEffect(flipKey) {
        animTarget = 0f       // reset
        animTarget = 1f       // animate to 1
    }
    val progress by animateFloatAsState(
        targetValue = animTarget,
        animationSpec = tween(durationMillis = FLIP_DURATION_MS, easing = FastOutSlowInEasing),
        label = "flipProgress"
    )

    // Map progress to rotation: -90° → 0°
    val rotation = -90f + (90f * progress)

    Box(
        modifier = Modifier
            .fillMaxWidth(0.82f)
            .aspectRatio(0.68f)
            .graphicsLayer {
                rotationY = rotation
                cameraDistance = 12 * density
            }
            .shadow(elevation = 12.dp, shape = RoundedCornerShape(20.dp))
            .clickable(enabled = !deckEmpty) { onTap() },
        contentAlignment = Alignment.Center
    ) {
        when {
            deckEmpty -> EmptyDeckFace()
            card == null -> CardBackFace()
            else -> CardFrontFace(card)
        }
    }
}

@Composable
private fun CardBackFace() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(CardBackRed, RoundedCornerShape(20.dp))
            .border(width = 6.dp, color = CardBackTrim, shape = RoundedCornerShape(20.dp))
            .padding(20.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "SORRY!",
            color = CardBackTrim,
            fontSize = 44.sp,
            fontWeight = FontWeight.Black,
            letterSpacing = 4.sp,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun EmptyDeckFace() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(CardCream, RoundedCornerShape(20.dp))
            .border(width = 3.dp, color = CardInk.copy(alpha = 0.15f), shape = RoundedCornerShape(20.dp))
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "DECK EMPTY",
                color = CardInk,
                fontSize = 28.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 3.sp,
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.height(12.dp))
            Text(
                text = "All 45 cards drawn.\nTap SHUFFLE to start again.",
                color = CardInk.copy(alpha = 0.7f),
                fontSize = 15.sp,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun CardFrontFace(card: Card) {
    val isSorry = card.isSorry
    val bg = if (isSorry) SorryRed else CardCream
    val ink = if (isSorry) Color.White else CardInk
    val accent = when (card.label) {
        "1", "5" -> AccentBlue
        "2", "10" -> AccentYellow
        "3", "11" -> Color(0xFF2E7D32)
        "4", "12" -> Color(0xFF6A1B9A)
        "7", "8" -> Color(0xFFD84315)
        else -> if (isSorry) CardBackTrim else CardInk
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(bg, RoundedCornerShape(20.dp))
            .border(width = 4.dp, color = accent, shape = RoundedCornerShape(20.dp))
            .padding(20.dp)
    ) {
        Text(
            text = card.label,
            color = ink,
            fontSize = if (isSorry) 18.sp else 22.sp,
            fontWeight = FontWeight.Black,
            modifier = Modifier.align(Alignment.TopStart)
        )
        Text(
            text = card.label,
            color = ink,
            fontSize = if (isSorry) 18.sp else 22.sp,
            fontWeight = FontWeight.Black,
            modifier = Modifier.align(Alignment.BottomEnd)
        )

        Column(
            modifier = Modifier.align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = card.label,
                color = ink,
                fontSize = if (isSorry) 64.sp else 96.sp,
                fontWeight = FontWeight.Black,
                fontFamily = FontFamily.SansSerif,
                textAlign = TextAlign.Center,
                letterSpacing = if (isSorry) 4.sp else 0.sp
            )
            Spacer(Modifier.height(12.dp))
            Text(
                text = card.title,
                color = ink,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = card.rule,
                color = ink.copy(alpha = 0.85f),
                fontSize = 13.sp,
                textAlign = TextAlign.Center,
                lineHeight = 18.sp
            )
        }
    }
}
