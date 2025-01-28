package com.arcx.uniwall.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp

@Composable
fun DataPoint(userName: String, userId: String, likes: Int, modifier: Modifier = Modifier) {

    val uriHandler = LocalUriHandler.current

    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(
                brush = Brush.linearGradient(
                    colors = listOf(Color.Black, Color.Black)
                ), alpha = 0.3f
            )
            .padding(horizontal = 6.dp)
    ) {
        Column {
            Row {
                val text = buildAnnotatedString {
                    append("Photo by ")
                    pushLink(
                        LinkAnnotation.Clickable(
                            tag = userName,
                            linkInteractionListener = {
                                uriHandler.openUri("https://unsplash.com/@$userId?utm_source=uniwall&utm_medium=referral")
                            }
                        )
                    )
                    withStyle(
                        SpanStyle(
                            textDecoration = TextDecoration.Underline
                        )
                    ) {
                        append(userName)
                    }
                    pop()
                    append(" on ")
                    pushLink(
                        LinkAnnotation.Clickable(
                            tag = "unsplash",
                            linkInteractionListener = {
                                uriHandler.openUri("https://unsplash.com/?utm_source=uniwall&utm_medium=referral")
                            }
                        )
                    )
                    withStyle(
                        SpanStyle(
                            textDecoration = TextDecoration.Underline
                        )
                    ) {
                        append("Unsplash")
                    }
                    pop()
                }
                Text(text, color = Color.White)
            }
            Spacer(Modifier.height(6.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.sizeIn(maxHeight = 32.dp)
            ) {
                Icon(Icons.Filled.Favorite, "Likes", tint = Color.Red)
                Spacer(Modifier.width(6.dp))
                Text("$likes likes", color = Color.White)
            }
            Spacer(Modifier.height(8.dp))
        }

    }
}