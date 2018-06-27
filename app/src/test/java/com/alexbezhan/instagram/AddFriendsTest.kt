package com.alexbezhan.instagram

import com.alexbezhan.instagram.data.Repository
import com.alexbezhan.instagram.models.User
import com.alexbezhan.instagram.screens.common.CommonLiveData
import com.alexbezhan.instagram.screens.profile.friends.AddFriendsViewModel
import org.junit.Test
import org.mockito.Mockito.*

class AddFriendsTest {
    @Test
    fun shouldAndFollowAndFollowerValue() {
        val repository = mock(Repository::class.java)
        val viewModel = AddFriendsViewModel(repository, mock(CommonLiveData::class.java))
        val user = User()

        val task = viewModel.toggleFollow(user, "1")
    }
}