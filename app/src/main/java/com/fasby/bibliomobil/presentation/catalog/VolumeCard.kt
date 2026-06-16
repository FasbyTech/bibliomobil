package com.fasby.bibliomobil.presentation.catalog

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.fasby.bibliomobil.data.local.entity.VolumeEntity

@Composable
fun VolumeCard(
    volume: VolumeEntity,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp)
        ) {
            // RENDERIZADO INTELIGENTE DE PORTADAS MEDIANTE COIL
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(volume.coverPath) // Acepta URLs de internet, URIs locales o archivos en crudo
                    .crossfade(true) // Animación suave de desvanecimiento al cargar la foto
                    .build(),
                contentDescription = "Portada de ${volume.title}",
                modifier = Modifier
                    .width(80.dp)
                    .fillMaxHeight(),
                contentScale = ContentScale.Crop, // Recorta y rellena el espacio sin deformar la portada
                // Recursos visuales opcionales para placeholders de carga
                placeholder = painterResource(id = android.R.drawable.ic_menu_gallery),
                error = painterResource(id = android.R.drawable.ic_dialog_alert)
            )

            // Panel de información textual al lado derecho de la portada
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(12.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = if (volume.number > 0) "${volume.title} #${volume.number}" else volume.title,
                        style = MaterialTheme.typography.titleMedium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis // Añade "..." si el título es demasiado largo
                    )
                    
                    Spacer(modifier = Modifier.height(2.dp))
                    
                    Text(
                        text = "Año: ${volume.publishedYear}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                // Fila inferior para pintar la puntuación por estrellas del usuario
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "⭐ ".repeat(volume.rating.coerceIn(1, 5)),
                        style = MaterialTheme.typography.bodyMedium
                    )
                    
                    if (volume.isRead) {
                        SuggestionChip(
                            onClick = { },
                            label = { Text("Leído", style = MaterialTheme.typography.labelSmall) },
                            modifier = Modifier.height(24.dp)
                        )
                    }
                }
            }
        }
    }
}
