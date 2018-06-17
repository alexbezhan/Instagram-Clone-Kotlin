package com.alexbezhan.instagram

import android.support.test.InstrumentationRegistry
import android.support.test.runner.AndroidJUnit4
import com.alexbezhan.instagram.activities.asFeedPost
import com.alexbezhan.instagram.activities.asNotification
import com.alexbezhan.instagram.activities.asUser
import com.alexbezhan.instagram.domain.Notifications
import com.alexbezhan.instagram.models.FeedPost
import com.alexbezhan.instagram.models.NotificationType
import com.alexbezhan.instagram.models.User
import com.alexbezhan.instagram.utils.firebase.FirebaseHelper.database
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ExampleInstrumentedTest {

    private lateinit var user1: User
    private lateinit var user2: User
    private lateinit var post: FeedPost
    private var notificationId: String? = null
    private lateinit var postLikeByUserRef: DatabaseReference

    @Before
    fun setUp() {
        val user1Email = "alex@alexbezhan.com"
        val user2Email = "dmitry@alexbezhan.com"

        FirebaseApp.initializeApp(InstrumentationRegistry.getTargetContext())

        if (FirebaseAuth.getInstance().currentUser == null) {
            FirebaseAuth.getInstance()
                    .signInWithEmailAndPassword(user1Email, "password")
                    .await()
        }
        user1 = database.child("users").orderByChild("email").equalTo(user1Email)
                .readOnce().children.first().asUser()!!
        user2 = database.child("users").orderByChild("email").equalTo(user2Email)
                .readOnce().children.first().asUser()!!
        post = database.child("feed-posts").child(user2.uid).limitToLast(1).readOnce()
                .children.first().asFeedPost()!!
        postLikeByUserRef = database.child("likes").child(post.id).child(user1.uid)

        println("user1: ${user1.uid}, user2: ${user2.uid}, post: ${post.id}")

    }

    @After
    fun tearDown() {
        notificationId?.let {
            Notifications.removeNotification(user2.uid, it)
            postLikeByUserRef.removeValue().await()
        }
        FirebaseAuth.getInstance().signOut()
    }

    @Test
    fun toggleNotificationsShouldFireASingleDatabaseChange() {
        val result = Notifications.toggleNotification(user1, user2.uid, NotificationType.LIKE,
                postLikeByUserRef, post).await()
        notificationId = result.notificationId
        val reads = database.child("notifications").child(user1.uid).readN(2)
        assertEquals(reads.size, 1)
    }
}