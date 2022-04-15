package com.tool.bl53.api

import com.tool.bl53.biz.bean.BL53LockInfo
import com.tool.bl53.network.BaseResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface Api {
    @GET(ApiConstants.QUERY_LOCK_INFO)
    suspend fun getBL53LockInfo(@Query("lockNo") lockNo: String): BaseResponse<BL53LockInfo>
//    @FormUrlEncoded
//    @POST(HttpsApi.login)
//    suspend fun login(
//        @Field("username") username: String,
//        @Field("password") password: String
//    ): BaseResponse<LoginBean>
//
//
//    /**
//     * 首页banner
//     */
//    @GET(HttpsApi.banner)
//    suspend fun getBannerJson(): BaseResponse<List<BannerBean>>
//
//    @GET(HttpsApi.gp)
//    suspend fun gp(): JhResponse<GBean>
//
//
//
//    /**
//     * 查看某个公众号历史数据
//     * https://wanandroid.com/wxarticle/list/408/1/json
//     */
//    @GET(HttpsApi.get_wechat_list_json + "{user_id}/{page}/json")
//    suspend fun getUserWechatArticleJson(
//        @Path("user_id") user_id: Int?,
//        @Path("page") page: Int
//    ): BaseResponse<BaseListResponse<MutableList<WechatPagerBean>>>
//
//
//
//    /**
//     * 项目列表数据
//     */
//    @GET(HttpsApi.get_project_cid_json + "{page}/json")
//    suspend fun getProjectCidJson(
//        @Path("page") page: Int,
//        @Query("cid") cid: Int?
//    ): BaseResponse<BaseListResponse<MutableList<ProjectPagerBean>>>
//
//
//    /**
//     * 搜索
//     */
//    @POST(HttpsApi.article_query + "{page}/json")
//    suspend fun getArticleQuery(
//        @Path("page") page: Int,
//        @Query("k") k: String = ""
//    ): BaseResponse<BaseListResponse<MutableList<SearchBean>>>

}