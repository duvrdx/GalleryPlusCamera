package souza.prospero.henrique.eduardo.galeria1

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import coil3.compose.AsyncImage

class DetailActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val imageUri = intent.getParcelableExtra<Uri>("imageUri")
            Scaffold(
                topBar = {
                    TopAppBar(
                        title = { Text("Detalhes da Imagem") },
                        actions = {
                            IconButton(onClick = {
                                imageUri?.let { shareImage(it) }
                            }) {
                                Icon(
                                    painter = painterResource(id = R.drawable.baseline_share_24),
                                    contentDescription = "Compartilhar"
                                )
                            }
                        }
                    )
                },
                content = { padding ->
                    if (imageUri != null) {
                        AsyncImage(
                            model = imageUri,
                            contentDescription = "Detailed Image",
                            modifier = Modifier.padding(padding).fillMaxSize(),
                            contentScale = ContentScale.Fit
                        )
                    } else {
                        Image(
                            painter = painterResource(id = R.drawable.baseline_broken_image_24),
                            contentDescription = "Error Image",
                            modifier = Modifier.padding(padding).fillMaxSize(),
                            contentScale = ContentScale.Fit
                        )
                    }
                }
            )
        }
    }

    private fun shareImage(imageUri: Uri) {
        val shareIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_STREAM, imageUri)
            type = "image/*" // Especifica que é uma imagem
            flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
        }
        startActivity(Intent.createChooser(shareIntent, "Compartilhar imagem"))
    }
}
