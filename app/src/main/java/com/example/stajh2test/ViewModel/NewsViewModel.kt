package com.example.stajh2test.ViewModel

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.stajh2test.ui.states.NewsItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.jsoup.Jsoup
import java.io.IOException

class NewsViewModel : ViewModel() {

    val newsList = mutableStateListOf<NewsItem>()
    val isLoading = mutableStateOf(false)
    val error = mutableStateOf<String?>(null)

    init {
        fetchNews()
    }

    fun fetchNews() {
        isLoading.value = true
        error.value = null
        newsList.clear()

        viewModelScope.launch(Dispatchers.IO) {
            try {
                // URL сторінки з новинами про стажування на Glavcom
                val url = "https://glavcom.ua/tags/stazh.html"

                // Додаємо User-Agent щоб сайт не відхиляв запит
                val doc = Jsoup.connect(url)
                    .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36")
                    .get()

                // Аналізуємо HTML структуру сайту Glavcom
                // Припускаємо, що новини знаходяться в блоках з певним класом
                val newsElements = doc.select("div.article-item")

                for (newsElement in newsElements) {
                    try {
                        // Отримуємо заголовок
                        val titleElement = newsElement.select("div.article-title a")
                        val title = titleElement.text()

                        // Отримуємо посилання
                        val linkHref = titleElement.attr("href")

                        // Повний URL новини
                        val fullUrl = if (linkHref.startsWith("http")) linkHref else "https://glavcom.ua$linkHref"

                        // Отримуємо дату публікації
                        val dateElement = newsElement.select("div.article-date")
                        val publishDate = dateElement.text()

                        // Перевіряємо чи усі необхідні дані отримані
                        if (title.isNotEmpty() && fullUrl.isNotEmpty()) {
                            // Створюємо об'єкт новини та додаємо до списку
                            val newsItem = NewsItem(
                                title = title,
                                publishDate = publishDate.ifEmpty { "Дата невідома" },
                                url = fullUrl
                            )

                            // Додаємо новину до списку в основному потоці
                            viewModelScope.launch(Dispatchers.Main) {
                                newsList.add(newsItem)
                            }
                        }
                    } catch (e: Exception) {
                        // Ігноруємо окремі помилки парсингу, щоб не зупиняти весь процес
                        e.printStackTrace()
                    }
                }

                // Перевіряємо чи вдалося отримати хоч якісь новини
                if (newsElements.isEmpty() || newsList.isEmpty()) {
                    // Якщо немає результатів, спробуємо інший селектор
                    val alternativeNewsElements = doc.select("div.lenta-item")

                    for (newsElement in alternativeNewsElements) {
                        try {
                            // Отримуємо заголовок
                            val titleElement = newsElement.select("div.lenta-title a")
                            val title = titleElement.text()

                            // Отримуємо посилання
                            val linkHref = titleElement.attr("href")

                            // Повний URL новини
                            val fullUrl = if (linkHref.startsWith("http")) linkHref else "https://glavcom.ua$linkHref"

                            // Отримуємо дату публікації
                            val dateElement = newsElement.select("div.lenta-date")
                            val publishDate = dateElement.text()

                            // Перевіряємо чи усі необхідні дані отримані
                            if (title.isNotEmpty() && fullUrl.isNotEmpty()) {
                                // Створюємо об'єкт новини та додаємо до списку
                                val newsItem = NewsItem(
                                    title = title,
                                    publishDate = publishDate.ifEmpty { "Дата невідома" },
                                    url = fullUrl
                                )

                                // Додаємо новину до списку в основному потоці
                                viewModelScope.launch(Dispatchers.Main) {
                                    newsList.add(newsItem)
                                }
                            }
                        } catch (e: Exception) {
                            // Ігноруємо окремі помилки парсингу
                            e.printStackTrace()
                        }
                    }
                }

                // Якщо все ще немає новин, спробуємо ще один підхід
                if (newsList.isEmpty()) {
                    // Спробуємо отримати будь-які елементи з посиланнями, які містять ключове слово "стаж"
                    val anyNewsLinks = doc.select("a:contains(стаж)")

                    for (link in anyNewsLinks) {
                        try {
                            val title = link.text()
                            val href = link.attr("href")

                            // Перевіряємо чи посилання не порожнє
                            if (title.isNotEmpty() && href.isNotEmpty()) {
                                val fullUrl = if (href.startsWith("http")) href else "https://glavcom.ua$href"

                                // Створюємо об'єкт новини та додаємо до списку
                                val newsItem = NewsItem(
                                    title = title,
                                    publishDate = "Дата не визначена",
                                    url = fullUrl
                                )

                                // Додаємо новину до списку в основному потоці
                                viewModelScope.launch(Dispatchers.Main) {
                                    newsList.add(newsItem)
                                }
                            }
                        } catch (e: Exception) {
                            // Ігноруємо окремі помилки
                            e.printStackTrace()
                        }
                    }
                }

                // Якщо все ще нічого не знайдено, додаємо тестову новину
                if (newsList.isEmpty()) {
                    viewModelScope.launch(Dispatchers.Main) {
                        newsList.add(
                            NewsItem(
                                title = "Не вдалося знайти новини по тегу 'стаж' на Glavcom",
                                publishDate = "Сьогодні",
                                url = "https://glavcom.ua/tags/stazh.html"
                            )
                        )
                    }
                }

                viewModelScope.launch(Dispatchers.Main) {
                    isLoading.value = false
                }
            } catch (e: IOException) {
                // Обробка помилок
                viewModelScope.launch(Dispatchers.Main) {
                    error.value = "Помилка завантаження новин: ${e.message}"
                    isLoading.value = false

                    // Додаємо тестову новину на випадок помилки
                    newsList.add(
                        NewsItem(
                            title = "Помилка завантаження: ${e.message}",
                            publishDate = "Сьогодні",
                            url = "https://glavcom.ua/tags/stazh.html"
                        )
                    )
                }
            }
        }
    }
}