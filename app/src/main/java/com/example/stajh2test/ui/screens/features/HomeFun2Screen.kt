@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.stajh2test.ui.screens.features

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.stajh2test.ViewModel.NewsViewModel
import com.example.stajh2test.ui.components.NewsCard
import com.example.stajh2test.ui.components.openUrlInBrowser
import com.example.stajh2test.ui.states.NewsItem

@Composable
fun HomeFun2Screen(navController: NavController) {
    val viewModel: NewsViewModel = viewModel()
    val context = LocalContext.current

    // Стан для відстеження, чи були спроби завантажити дані
    var attemptedLoading by remember { mutableStateOf(false) }

    // Тестові дані для випадку, якщо не вдасться завантажити з сайту
    val testNewsList = remember {
        listOf(
            NewsItem(
                title = "Оголошено набір на літнє стажування 2025 для студентів IT-спеціальностей",
                publishDate = "14:30 17.04.2025",
                url = "https://glavcom.ua/tags/stazh.html"
            ),
            NewsItem(
                title = "Програма стажування для молодих юристів відкриває нові можливості",
                publishDate = "09:15 16.04.2025",
                url = "https://glavcom.ua/tags/stazh.html"
            ),
            NewsItem(
                title = "Найкращі програми стажування 2025 року: огляд можливостей",
                publishDate = "11:45 15.04.2025",
                url = "https://glavcom.ua/tags/stazh.html"
            ),
            NewsItem(
                title = "Міністерство цифрової трансформації запускає нову програму стажування",
                publishDate = "16:20 14.04.2025",
                url = "https://glavcom.ua/tags/stazh.html"
            ),
            NewsItem(
                title = "Як успішно пройти співбесіду на стажування: поради експертів",
                publishDate = "10:00 13.04.2025",
                url = "https://glavcom.ua/tags/stazh.html"
            )
        )
    }

    // Ефект для встановлення флагу після спроби завантаження
    LaunchedEffect(viewModel.isLoading.value) {
        if (!viewModel.isLoading.value) {
            attemptedLoading = true
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Новини про стаж") }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (viewModel.isLoading.value) {
                // Відображення індикатора завантаження
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.align(Alignment.Center)
                ) {
                    CircularProgressIndicator()
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Завантаження новин...")
                }
            } else if (viewModel.error.value != null && viewModel.newsList.isEmpty()) {
                // Відображення помилки та тестових даних
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(16.dp)
                ) {
                    Text(
                        text = "Виникла помилка при завантаженні: ${viewModel.error.value}",
                        color = Color.Red,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    TextButton(onClick = { viewModel.fetchNews() }) {
                        Text("Спробувати знову")
                    }

                    Text(
                        text = "Відображаємо тестові дані:",
                        modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
                    )

                    // Відображаємо тестові дані
                    LazyColumn {
                        itemsIndexed(testNewsList) { _, newsItem ->
                            NewsCard(
                                newsItem = newsItem,
                                onReadMoreClick = { url ->
                                    openUrlInBrowser(context, url)
                                }
                            )
                        }
                    }
                }
            } else if (viewModel.newsList.isEmpty() && attemptedLoading) {
                // Якщо після завантаження список порожній - показуємо тестові дані
                Column(
                    modifier = Modifier.fillMaxSize()
                ) {
                    Text(
                        text = "Не знайдено новин про стажування. Відображаємо тестові дані:",
                        modifier = Modifier.padding(16.dp)
                    )

                    LazyColumn {
                        itemsIndexed(testNewsList) { _, newsItem ->
                            NewsCard(
                                newsItem = newsItem,
                                onReadMoreClick = { url ->
                                    openUrlInBrowser(context, url)
                                }
                            )
                        }
                    }
                }
            } else {
                // Відображення реальних новин, якщо вони є
                LazyColumn(
                    modifier = Modifier.fillMaxSize()
                ) {
                    itemsIndexed(viewModel.newsList) { _, newsItem ->
                        NewsCard(
                            newsItem = newsItem,
                            onReadMoreClick = { url ->
                                openUrlInBrowser(context, url)
                            }
                        )
                    }
                }
            }
        }
    }
}