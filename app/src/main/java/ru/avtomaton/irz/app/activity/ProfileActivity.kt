package ru.avtomaton.irz.app.activity

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Bitmap
import android.os.Bundle
import android.text.SpannableStringBuilder
import android.view.View
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import ru.avtomaton.irz.app.R
import ru.avtomaton.irz.app.activity.util.NewsFeedAdapter
import ru.avtomaton.irz.app.activity.util.NewsSearchParams
import ru.avtomaton.irz.app.client.IrzHttpClient.loadImageBy
import ru.avtomaton.irz.app.databinding.ActivityProfileBinding
import ru.avtomaton.irz.app.databinding.CareerPathElementBinding
import ru.avtomaton.irz.app.databinding.ChangeUserInfoBinding
import ru.avtomaton.irz.app.model.OpResult
import ru.avtomaton.irz.app.model.pojo.User
import ru.avtomaton.irz.app.model.pojo.UserInfo
import ru.avtomaton.irz.app.model.repository.MessengerRepository
import ru.avtomaton.irz.app.model.repository.SubscriptionsRepository
import ru.avtomaton.irz.app.model.repository.UserRepository
import ru.avtomaton.irz.app.services.CredentialsManager
import java.util.*

/**
 * @author Anton Akkuzin
 */
open class ProfileActivity : NavbarAppCompatActivityBase() {

    private lateinit var binding: ActivityProfileBinding
    private lateinit var user: User
    private lateinit var defaultImage: Bitmap
    private lateinit var selectedColor: ColorStateList
    private lateinit var nonSelectedColor: ColorStateList
    private var isSubscription: Boolean = false

    private lateinit var emptyFields: String
    private lateinit var imageLoadError: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val id: UUID? = intent.getStringExtra("id")!!.run {
            if (isEmpty()) null else UUID.fromString(this)
        }
        imageLoadError = getString(R.string.common_on_image_upload_error)
        emptyFields = getString(R.string.common_on_empty_fields_left)
        selectedColor = ContextCompat.getColorStateList(this, R.color.Pantone_289_C)!!
        nonSelectedColor = ContextCompat.getColorStateList(this, R.color.Cool_Grey_3)!!
        binding = ActivityProfileBinding.inflate(layoutInflater).apply {
            defaultImage = image.drawable.toBitmap()
            newsButton.setOnClickListener { onNewsClick() }
            messengerButton.setOnClickListener { onMessengerClick() }
            searchButton.setOnClickListener { onSearchClick() }
            profileButton.setOnClickListener { onProfileClick() }
            profileEditButton.setOnClickListener {
                ChangeUserInfoBinding.inflate(layoutInflater).showDialog()
            }
            profileMessageButton.setOnClickListener { async { openChat() } }
            aboutMyselfButton.setOnClickListener { sectionClick(Section.INFO) }
            profileNewsButton.setOnClickListener { sectionClick(Section.NEWS) }
            careerPathButton.setOnClickListener { sectionClick(Section.CAREER_PATH) }
            if (id == null) {
                profileActionButton.setOnClickListener { async { exit() } }
                profileActionLogo.setImageResource(R.drawable.exit)
                profileActionLogo.setBackgroundColor(
                    resources.getColor(R.color.Pantone_200_C, null)
                )
            } else {
                profileActionLogo.setImageResource(R.drawable.pin)
                profileEditButton.visibility = View.GONE
            }
            async { bindAllInfo(id) }
            setContentView(root)
        }
    }

    private suspend fun ActivityProfileBinding.bindAllInfo(id: UUID?) {
        setUserInfo(id)
        careerPathElements.bindCareerPath()
        newsFeed.apply {
            layoutManager = LinearLayoutManager(this@ProfileActivity)
            adapter = NewsFeedAdapter(
                this@ProfileActivity,
                NewsSearchParams.Builder().apply { authorId = user.id })
        }
        aboutMyself.visibility = View.VISIBLE
    }

    private fun LinearLayout.bindCareerPath() {
        user.positions.forEach {
            CareerPathElementBinding.inflate(layoutInflater).apply {
                @SuppressLint("SetTextI18n")
                startDate.text = "С ${dateFormatV2.format(it.start)}"
                positionName.text = it.name
            }.let { this.addView(it.root) }
        }
    }

    private suspend fun exit() {
        CredentialsManager.exit()
        startActivity(NewsActivity.openNews(this))
    }

    private suspend fun onSubscribe() {
        SubscriptionsRepository.apply {
            val success = if (isSubscription) unsubscribe(user.id) else subscribe(user.id)
            if (success) {
                isSubscription = !isSubscription
                setSubscriptionBackground()
            }
        }
    }

    private suspend fun ActivityProfileBinding.setUserInfo(id: UUID?) {
        sectionMain.visibility = View.GONE
        aboutMyself.visibility = View.GONE
        getUser(id).letIfSuccess { bindUserInfo(this) }
    }

    private suspend fun getUser(id: UUID?): OpResult<User> =
        if (id == null)
            UserRepository.getMe()
        else
            UserRepository.getUser(id)

    private fun ActivityProfileBinding.bindUserInfo(currentUser: User) {
        birthday.text = dateFormatV2.format(currentUser.birthday)
        aboutMyselfText.text = currentUser.aboutMyself
        profileSkillsText.text = currentUser.skills
        profileMyDoingsText.text = currentUser.myDoings
        image.setImageBitmap(defaultImage)
        currentUser.imageId?.also {
            Glide.with(this@ProfileActivity).loadImageBy(it).error(defaultImage).centerCrop() .into(image)
        }
        name.text = currentUser.firstName
        surname.text = currentUser.surname
        patronymic.text = currentUser.patronymic
        mainPosition.text =
            currentUser.positions.let { if (it.isEmpty()) threeDots else it[0].name }

        if (!currentUser.isMe) {
            isSubscription = currentUser.isSubscription
            profileActionButton.setOnClickListener {
                async { this@ProfileActivity.onSubscribe() }
            }
            setSubscriptionBackground()
            profileEditButton.visibility = View.GONE
            profileMessageButton.visibility = View.VISIBLE
        } else {
            profileEditButton.visibility = View.VISIBLE
            profileMessageButton.visibility = View.GONE
        }
        user = currentUser
        sectionMain.visibility = View.VISIBLE
        aboutMyself.visibility = View.VISIBLE
    }

    private fun setSubscriptionBackground() {
        val color = if (isSubscription)
            R.color.Pantone_200_C
        else
            R.color.Pantone_289_C
        binding.profileActionLogo.setBackgroundColor(resources.getColor(color, null))
    }

    private fun ChangeUserInfoBinding.showDialog() {
        aboutMyselfText.text = SpannableStringBuilder(user.aboutMyself)
        profileMyDoingsText.text = SpannableStringBuilder(user.myDoings)
        profileSkillsText.text = SpannableStringBuilder(user.skills)
        user.imageId?.also { Glide.with(this@ProfileActivity).loadImageBy(it).into(image) }
        newPhotoButton.setOnClickListener { contract.launch("image/*") }
        onImageUploaded = { Glide.with(this@ProfileActivity).load(it).into(image) }
        deletePhotoButton.setOnClickListener {
            imageUri = null
            Glide.with(this@ProfileActivity).load(defaultImage).into(image)
        }
        AlertDialog.Builder(this@ProfileActivity).create().apply {
            setView(this@showDialog.root)
            setButton(AlertDialog.BUTTON_POSITIVE, "OK") { _, _ ->
                kotlin.run {
                    async { this@showDialog.updateInfo() }
                }
            }
        }.show()
    }

    private suspend fun ChangeUserInfoBinding.updateInfo() {
        val aboutMyself = aboutMyselfText.text.toString()
        val myDoings = profileMyDoingsText.text.toString()
        val skills = profileSkillsText.text.toString()
        if (aboutMyself.isBlank() || myDoings.isBlank() || skills.isBlank()) {
            warn(emptyFields)
            return
        }
        if (!UserRepository.updateInfo(UserInfo(aboutMyself, myDoings, skills))) {
            error()
            return
        }
        if (!processPhoto()) {
            error()
            return
        }
        this@ProfileActivity.binding.setUserInfo(user.id)
    }

    private suspend fun processPhoto(): Boolean {
        if (imageUri == null) {
            return UserRepository.deletePhoto()
        }
        val image = imageUri?.toImageBytes() ?: return false

        return UserRepository.updatePhoto(image)
    }

    private fun ActivityProfileBinding.sectionClick(section: Section) {
        when (section) {
            Section.INFO -> {
                aboutMyselfButton.backgroundTintList = selectedColor
                profileNewsButton.backgroundTintList = nonSelectedColor
                careerPathButton.backgroundTintList = nonSelectedColor
                aboutMyself.visibility = View.VISIBLE
                news.visibility = View.GONE
                careerPath.visibility = View.GONE
            }
            Section.CAREER_PATH -> {
                aboutMyselfButton.backgroundTintList = nonSelectedColor
                profileNewsButton.backgroundTintList = nonSelectedColor
                careerPathButton.backgroundTintList = selectedColor
                aboutMyself.visibility = View.GONE
                news.visibility = View.GONE
                careerPath.visibility = View.VISIBLE
            }
            Section.NEWS -> {
                aboutMyselfButton.backgroundTintList = nonSelectedColor
                profileNewsButton.backgroundTintList = selectedColor
                careerPathButton.backgroundTintList = nonSelectedColor
                aboutMyself.visibility = View.GONE
                news.visibility = View.VISIBLE
                careerPath.visibility = View.GONE
            }
        }
    }

    private suspend fun openChat() {
        val me = UserRepository.getMe()
        if (me.isFailure) {
            error()
            return
        }
        MessengerRepository.getChatFor(user.id).letIfSuccess {
            val intent = ChatActivity.open(
                this@ProfileActivity,
                this,
                me.value().id, user.id
            )
            startActivity(intent)
        }
    }

    private enum class Section {
        INFO,
        NEWS,
        CAREER_PATH,
    }

    override fun onProfileClick() {
        if (!user.isMe) {
            super.onProfileClick()
        }
        // or do nothing
    }

    companion object {

        fun openMyProfile(context: Context): Intent {
            val intent = Intent(context, ProfileActivity::class.java)
            intent.putExtra("id", "")
            return intent
        }

        fun openProfile(context: Context, id: UUID): Intent {
            val intent = Intent(context, ProfileActivity::class.java)
            intent.putExtra("id", id.toString())
            return intent
        }
    }
}