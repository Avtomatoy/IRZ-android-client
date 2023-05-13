package ru.avtomaton.irz.app.activity

/**
 * @author Anton Akkuzin
 */
open class NavbarAppCompatActivityBase: AppCompatActivityBase(), NavbarListener {

    override fun onNewsClick() {
        startActivity(NewsActivity.openNews(this))
    }

    override fun onMessengerClick() {
        startActivity(MessengerActivity.open(this))
    }

    override fun onSearchClick() {
        startActivity(SearchActivity.open(this))
    }

    override fun onProfileClick() {
        startActivity(ProfileActivity.openMyProfile(this))
    }
}