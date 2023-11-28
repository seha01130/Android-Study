package com.example.flo.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.flo.data.entities.Album
import com.example.flo.data.entities.Like

@Dao
interface AlbumDao {
    @Insert
    fun insert(album: Album)

    @Update
    fun update(album: Album)

    @Delete
    fun delete(album: Album)

    @Query("SELECT * FROM AlbumTable") // 테이블의 모든 값을 가져와라
    fun getAlbums(): List<Album>

    @Query("SELECT * FROM AlbumTable WHERE id = :id")
    fun getAlbum(id: Int): Album

    @Insert //LikeTable에다가 사용자가 좋아요 한 데이터를 추가해주는 INSERT문
    fun likeAlbum(like: Like)

    //AlbumFragment에 들어갈 때 현재 사용자가 이 앨범을 좋아요 눌렀는지 안눌렀는지 확인해줌
    //현재 사용자를 뜻하는 userId와 앨범을 뜻하는 albumId를 비교해서 이와같은 userId와 albumId가 LikeTable에 있는지 확인해서
    //LikeTable의 id값을 반환해줘
    //정보가 없으면 null을 반환하겠죠 -> null처리를 위해 ? 붙임
    @Query("SELECT id FROM LikeTable WHERE userId = :userId AND albumId = :albumId")
    fun isLikedAlbum(userId: Int, albumId: Int): Int?

    //DELETE해줌
    @Query("DELETE FROM LikeTable WHERE userId = :userId AND albumId = :albumId")
    fun disLikeAlbum(userId: Int, albumId: Int)

    //보관함에서 User를 구분해서 좋아요 한 앨범에 대한 정보를 가져옴
    @Query("SELECT AT.* FROM LikeTable as LT LEFT JOIN AlbumTable as AT ON LT.albumId = AT.id WHERE LT.userId = :userId")
    fun getLikedAlbums(userId: Int): List<Album>
}