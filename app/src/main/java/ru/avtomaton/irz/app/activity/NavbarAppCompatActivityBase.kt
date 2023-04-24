package ru.avtomaton.irz.app.activity

/**
 * @author Anton Akkuzin
 */
open class NavbarAppCompatActivityBase: AppCompatActivityBase(), NavbarListener {

    override fun onNewsClick() {
        startActivity(NewsActivity.openNews(this))
    }

    override fun onMessengerClick() {
        TODO("Not yet implemented")
    }

    override fun onSearchClick() {
        TODO("Not yet implemented")
    }

    override fun onEventsClick() {
        TODO("Not yet implemented")
    }

    override fun onProfileClick() {
        startActivity(ProfileActivity.openMyProfile(this))
    }
}