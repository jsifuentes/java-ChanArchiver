package com.jsifuentes.fourchan;

/**
 * Created by Jacob on 11/17/2014.
 */
public class FourChanPost {
    protected Long PostID;
    protected String Name;
    protected String UserID;
    protected String Subject;
    protected String Capcode;
    protected String Tripcode;
    protected String Comment;
    protected Long ReplyTo;
    protected String PostDate;
    protected Long BigTimestamp;
    protected Long SmallTimestamp;
    protected String CountryName;
    protected String Country;
    protected Boolean Spoiler = false;
    protected String ImageExtension;
    protected String ImageFilename;
    protected Long ImageSize;
    protected Integer ImageWidth;
    protected Integer ImageHeight;
    protected String ImageMD5;
    protected Integer ThumbWidth;
    protected Integer ThumbHeight;
    protected Boolean FileDeleted = false;

    public Long getPostID() {
        return this.PostID;
    }

    public String getName() {
        return this.Name;
    }

    public String getUserID() {
        return this.UserID;
    }

    public String getSubject() {
        return this.Subject;
    }

    public String getCapcode() {
        return this.Capcode;
    }

    public String getTripcode() {
        return this.Tripcode;
    }

    public String getComment() {
        return this.Comment;
    }

    public Long getReplyTo() {
        return this.ReplyTo;
    }

    public String getPostDate() {
        return this.PostDate;
    }

    public Long getBigTimestamp() {
        return this.BigTimestamp;
    }

    public Long getSmallTimestamp() {
        return this.SmallTimestamp;
    }

    public String getCountryName() {
        return this.CountryName;
    }

    public String getCountry() {
        return this.Country;
    }

    public Boolean getSpoiler() {
        return this.Spoiler;
    }

    public String getImageExtension() {
        return this.ImageExtension;
    }

    public String getImageFilename() {
        return this.ImageFilename;
    }

    public Long getImageSize() {
        return this.ImageSize;
    }

    public Integer getImageWidth() {
        return this.ImageWidth;
    }

    public Integer getImageHeight() {
        return this.ImageHeight;
    }

    public String getImageMD5() {
        return this.ImageMD5;
    }

    public Integer getThumbWidth() {
        return this.ThumbWidth;
    }

    public Integer getThumbHeight() {
        return this.ThumbHeight;
    }

    public Boolean isFileDeleted() {
        return this.FileDeleted;
    }

    public void setPostID(Long postID) {
        this.PostID = postID;
    }

    public void setName(String name) {
        this.Name = name;
    }

    public void setUserID(String userID) { this.UserID = userID; }

    public void setSubject(String subject) {
        this.Subject = subject;
    }

    public void setCapcode(String capcode) {
        this.Capcode = capcode;
    }

    public void setTripcode(String tripcode) {
        this.Tripcode = tripcode;
    }

    public void setComment(String comment) {
        this.Comment = comment;
    }

    public void setReplyTo(Long replyTo) {
        this.ReplyTo = replyTo;
    }

    public void setPostDate(String postDate) {
        this.PostDate = postDate;
    }

    public void setBigTimestamp(Long bigTimestamp) {
        this.BigTimestamp = bigTimestamp;
    }

    public void setSmallTimestamp(Long smallTimestamp) {
        this.SmallTimestamp = smallTimestamp;
    }

    public void setCountryName(String countryName) {
        this.CountryName = countryName;
    }

    public void setCountry(String country) {
        this.Country = country;
    }

    public void setSpoiler(Boolean spoiler) {
        this.Spoiler = spoiler;
    }

    public void setImageExtension(String imageExtension) {
        this.ImageExtension = imageExtension;
    }

    public void setImageFilename(String imageFilename) {
        this.ImageFilename = imageFilename;
    }

    public void setImageSize(Long imageSize) {
        this.ImageSize = imageSize;
    }

    public void setImageWidth(Integer imageWidth) {
        this.ImageWidth = imageWidth;
    }

    public void setImageHeight(Integer imageHeight) {
        this.ImageHeight = imageHeight;
    }

    public void setImageMD5(String imageMD5) {
        this.ImageMD5 = imageMD5;
    }

    public void setThumbWidth(Integer thumbWidth) {
        this.ThumbWidth = thumbWidth;
    }

    public void setThumbHeight(Integer thumbHeight) {
        this.ThumbHeight = thumbHeight;
    }

    public void setFileDeleted(Boolean fileDeleted) {
        this.FileDeleted = fileDeleted;
    }
}
