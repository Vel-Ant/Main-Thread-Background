package ru.netology.nmedia.imageview

import android.widget.ImageView
import com.bumptech.glide.Glide
import ru.netology.nmedia.R

fun ImageView.LoadImageAvatar(url: String) {
    Glide.with(this)
        .load(url)
        .placeholder(R.drawable.ic_loading_100dp)
        .error(R.drawable.ic_error_100dp)
        .circleCrop()
        .timeout(6_000)
        .into(this)
}

fun ImageView.LoadImageAttachment(url: String) {
    Glide.with(this)
        .load(url)
        .timeout(6_000)
        .into(this)
}

//методы в Glide

//    Glide.with(context)
//        .load(url)  // адрес изображения
//        .placeholder(R.drawable.ic_loading_100dp)   // временное изображение во время загрузки основного изображения
//        .error(R.drawable.ic_error_100dp)   // изображение при возникновении ошибки
//        .transform()    // Множественные трансформации
//        .circleCrop()   // округление изображения
//        .timeout(1_000)     // задержка вывода изображения в мс
//        .centerCrop()   // Метод масштабирует изображение до размера ImageView, но изображение может быть неполным
//        .fitCenter()    // Увеличить изображение до размера, меньшего или равного ImageView
//        .thumbnail( 0.2f )      // отображение в %% от исходного размера изображения
//        Override (width, height) // изменение размера изображения перед его отображением. Здесь используется единица измерения px
//        .skipMemoryCache(true)      // Glide не будет помещать изображение в кэш-память
//        .diskCacheStrategy( DiskCacheStrategy.NONE )    // отключение дискового кеша. Использовать вместе с .skipMemoryCache(true)
//        .DiskCacheStrategy.SOURCE   // кэширует только полноразмерные изображения
//        .DiskCacheStrategy.RESULT   // кэширует только окончательный график загрузки
//        .DiskCacheStrategy.ALL      // кэширует все карты версий (Поведение по умолчанию）
//        .into(imageView)