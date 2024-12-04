package souza.prospero.henrique.eduardo.galeria1

import android.content.Context
import android.content.pm.PackageManager
import android.Manifest
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import coil3.compose.AsyncImage
import java.io.File

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                val context = LocalContext.current
                var images by remember { mutableStateOf(loadImages(context)) }

                val cameraLauncher =
                    rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) { success ->
                        if (success) {
                            images = loadImages(context)
                        }
                    }

                val permissionLauncher = rememberLauncherForActivityResult(
                    ActivityResultContracts.RequestPermission()
                ) {
                    if (it) {
                        Toast.makeText(context, "Permissão concedida", Toast.LENGTH_SHORT).show()
                        // Crie o arquivo aqui
                        val file = context.createImageFile()
                        val uri = FileProvider.getUriForFile(
                            context,
                            "souza.prospero.henrique.eduardo.galeria1.provider", // Substitua por applicationId se necessário
                            file
                        )
                        cameraLauncher.launch(uri)
                    } else {
                        Toast.makeText(context, "Permissão negada", Toast.LENGTH_SHORT).show()
                    }
                }

                Column {
                    GalleryScreen(
                        images = images,
                        onCaptureImage = {
                            val permissionCheckResult =
                                ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA)
                            if (permissionCheckResult == PackageManager.PERMISSION_GRANTED) {
                                // Crie o arquivo aqui
                                val file = context.createImageFile()
                                val uri = FileProvider.getUriForFile(
                                    context,
                                    "souza.prospero.henrique.eduardo.galeria1.provider", // Substitua por applicationId se necessário
                                    file
                                )
                                cameraLauncher.launch(uri)
                            } else {
                                permissionLauncher.launch(Manifest.permission.CAMERA)
                            }
                        }
                    )
                }
            }
        }

    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GalleryScreen(images: List<File>, modifier: Modifier = Modifier, onCaptureImage: () -> Unit) {
    val context = LocalContext.current
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Galeria") },
                actions = {
                    IconButton(onClick = onCaptureImage) {
                        Icon(
                            painter = painterResource(id = R.drawable.baseline_camera_alt_24),
                            contentDescription = "Tirar Foto"
                        )
                    }
                }
            )
        },
        content = { paddingValue ->
            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                modifier = modifier.padding(paddingValue).padding(8.dp)
            ) {
                items(images) { image ->
                    ImageCard(image, context)
                }
            }
        }
    )
}


@Composable
fun ImageCard(file: File, context: Context) {
    AsyncImage(
        model = file,
        contentDescription = "Image",
        modifier = Modifier
            .padding(4.dp)
            .size(120.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(Color.Gray)
            .clickable {
                val uri = FileProvider.getUriForFile(
                    context,
                    "souza.prospero.henrique.eduardo.galeria1.provider",
                    file
                )
                val intent = Intent(context, DetailActivity::class.java).apply {
                    putExtra("imageUri", uri)
                }
                context.startActivity(intent)
            },
        contentScale = ContentScale.Crop
    )
}

fun loadImages(context: Context): List<File> {
    val fileDir = File(context.filesDir, "my_gallery")
    return fileDir.listFiles()?.filter { it.isFile && it.extension in listOf("jpg", "png") } ?: emptyList()
}

@Composable
@Preview
fun GalleryScreenPreview() {
    val mockFiles = List(6) { index ->
        File("/mock/path/image_$index.jpg")
    }
    MaterialTheme {
        GalleryScreen(images = mockFiles, onCaptureImage = {})
    }
}


fun Context.createImageFile(): File {
    val fileDir = File(filesDir, "my_gallery")

    if (!fileDir.exists()) {
        fileDir.mkdirs()
    }
    return File.createTempFile(
        "JPEG_${System.currentTimeMillis()}_",
        ".jpg",
        fileDir
    )
}