package com.example.tictactoe

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.tictactoe.ui.theme.TicTacToeTheme
import kotlinx.coroutines.delay
import kotlin.random.Random

enum class Win {
    PLAYER,
    COMPUTER,
    DRAW
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TicTacToeTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background
                ) {

                    TTTScreen()

                }
            }
        }
    }
}

@Composable
fun TTTScreen() {

    val win = remember { mutableStateOf<Win?>(null) }

    //true - players turn
    //false - computer turn
    val playerTurn = remember { mutableStateOf(true) }

    //true -player move, false - computer move, null - no move
    val moves = remember {
        mutableStateListOf<Boolean?>(
            null, null, null, null, null, null, null, null, null
        )
    }

    //USER MOVE
    val onTap: (Offset) -> Unit =
        {
            if (playerTurn.value && win.value == null) {
                val x = (it.x / 333).toInt()
                val y = (it.y / 333).toInt()
                val posInMoves = y * 3 + x

                if (moves[posInMoves] == null) {
                    moves[posInMoves] = true
                    playerTurn.value = false

                    win.value = checkEndGame(moves)

                }

            }
        }


    Column(horizontalAlignment = Alignment.CenterHorizontally) {

        Text(text = "Tic Tac Toe", fontSize = 20.sp, modifier = Modifier.padding(10.dp))

        Header(playerTurn.value)

        Board(moves, onTap)

        //COMPUTER MOVE
        if (!playerTurn.value && moves.contains(null) && win.value == null) {
            CircularProgressIndicator(modifier = Modifier.padding(16.dp))


            val coroutineScope = rememberCoroutineScope()
            LaunchedEffect(key1 = Unit) {
                delay(1200)

                var random = Random.nextInt(0, 9)

                while (moves[random] != null) {
                    random = Random.nextInt(0, 9)
                }

                moves[random] = false
                playerTurn.value = true

                win.value = checkEndGame(moves)
            }

        }


        when(win.value)
        {
            Win.PLAYER -> { Text(text = "Player has won")}

            Win.COMPUTER -> {Text(text = "Computer has won")}

            Win.DRAW -> {
                Text(text = "It's a draw")}

            else -> {}
        }



        //Restart button
        Button(onClick = {
            playerTurn.value = true;
            for (i in 0..8) {
                moves[i] = null
            }
            win.value = null
        }) {
            Text(text = "Restart")
        }
    }
}

fun checkEndGame(m: List<Boolean?>): Win? {

    var win : Win? = null

    if (
        (m[0] == true && m[1] == true && m[2] == true) ||
        (m[3] == true && m[4] == true && m[5] == true) ||
        (m[6] == true && m[7] == true && m[8] == true) ||
        (m[0] == true && m[3] == true && m[6] == true) ||
        (m[1] == true && m[4] == true && m[7] == true) ||
        (m[2] == true && m[5] == true && m[8] == true) ||
        (m[0] == true && m[4] == true && m[8] == true) ||
        (m[2] == true && m[4] == true && m[6] == true)
    )
        win = Win.PLAYER

    if ((m[0] == false && m[1] == false && m[2] == false) ||
        (m[3] == false && m[4] == false && m[5] == false) ||
        (m[6] == false && m[7] == false && m[8] == false) ||
        (m[0] == false && m[3] == false && m[6] == false) ||
        (m[1] == false && m[4] == false && m[7] == false) ||
        (m[2] == false && m[5] == false && m[8] == false) ||
        (m[0] == false && m[4] == false && m[8] == false) ||
        (m[2] == false && m[4] == false && m[6] == false)
    )
        win = Win.COMPUTER

    if ( win == null && !m.contains(null))
    {win = Win.DRAW}

    return win
}

@Composable
fun Header(playerTurn: Boolean) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        val playerBoxColor = if (playerTurn) Color.Green else Color.LightGray
        val computerBoxColor = if (playerTurn) Color.LightGray else Color.Green

        Box(
            modifier = Modifier
                .width(100.dp)
                .background(playerBoxColor)
        ) {
            Text(
                text = "Player", modifier = Modifier
                    .align(Alignment.Center)
                    .padding(10.dp)
            )
        }

        Box(
            modifier = Modifier
                .width(100.dp)
                .background(computerBoxColor)
        ) {
            Text(
                text = "Computer", modifier = Modifier
                    .align(Alignment.Center)
                    .padding(10.dp)
            )
        }
    }
}

@Composable
fun Board(moves: List<Boolean?>, onTap: (Offset) -> Unit) {
    Box(
        modifier = Modifier
            .aspectRatio(1f)
            .padding(32.dp)
            .background(Color.LightGray)
            .pointerInput(Unit) {
                detectTapGestures(onTap = onTap)
            }
    ) {
        Column(verticalArrangement = Arrangement.SpaceEvenly, modifier = Modifier.fillMaxSize(1f))
        {
            Row(
                modifier = Modifier
                    .height(2.dp)
                    .fillMaxWidth(1f)
                    .background(Color.Black)
            ) {}
            Row(
                modifier = Modifier
                    .height(2.dp)
                    .fillMaxWidth(1f)
                    .background(Color.Black)
            ) {}
        }
        Row(horizontalArrangement = Arrangement.SpaceEvenly, modifier = Modifier.fillMaxSize(1f))
        {
            Column(
                modifier = Modifier
                    .width(2.dp)
                    .fillMaxHeight(1f)
                    .background(Color.Black)
            ) {}
            Column(
                modifier = Modifier
                    .width(2.dp)
                    .fillMaxHeight(1f)
                    .background(Color.Black)
            ) {}
        }
        Column {
            for (i in 0..2) {
                Row(modifier = Modifier.weight(1f)) {
                    for (j in 0..2) {
                        Column(modifier = Modifier.weight(1f)) {
                            getComposableFromMove(move = moves[i * 3 + j])
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun getComposableFromMove(move: Boolean?) {
    when (move) {
        true -> Image(
            painter = painterResource(id = R.drawable.x),
            contentDescription = null,
            colorFilter = ColorFilter.tint(Color.Blue),
            modifier = Modifier.fillMaxSize(1f)
        )

        false -> Image(
            painter = painterResource(id = R.drawable.o),
            contentDescription = null,
            colorFilter = ColorFilter.tint(Color.Red),
            modifier = Modifier.fillMaxSize(1f)

        )

        null -> Image(painterResource(id = R.drawable.empty), contentDescription = null)
    }
}

