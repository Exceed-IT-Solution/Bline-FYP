package com.example.blind.Utils;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface Api {


    // Login Page API
    @FormUrlEncoded
    @POST("ProductData")
    Call<ResponseBody> CurrencyNote(
            @Field("ID") String str_noteNumber
    );
}
