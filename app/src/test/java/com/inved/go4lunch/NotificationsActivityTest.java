package com.inved.go4lunch;

import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.inved.go4lunch.controller.ViewPlaceActivity;
import com.inved.go4lunch.notification.NotificationsActivity;
import com.inved.go4lunch.utils.UnitConversion;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;

/*
@RunWith(RobolectricTestRunner.class)
public class NotificationsActivityTest {
/*
    private NotificationsActivity notificationsActivity;

    @Before
    public void setUp() {
        notificationsActivity = Robolectric.buildActivity(NotificationsActivity.class)
                .create()
                .visible()
                .get();
    }*/

  /*  @Test
    public void should_Show_Notification_With_Good_Values() {


        //Given
        String restaurantName = "Alibaba";
        String restaurantVicinity = "4 rue de la Foire aux Lions, 57000 Metz";
        String placeId="ChIJxdk03O0zlUcR14KvLin2O7Q";
        Button btnSeeRestaurantChoosen = notificationsActivity.findViewById(R.id.activity_notification_btn_see_choice);
        TextView notificationMessageText = notificationsActivity.findViewById(R.id.activity_notification_message_text);

        //When


        /**DON4T WORKS HERE*/
       // notificationsActivity.showNotificationMessageText(restaurantName,restaurantVicinity,placeId);

        //Then

     /*   Assert.assertEquals(View.INVISIBLE, btnSeeRestaurantChoosen.getVisibility());

    }*/
//}
