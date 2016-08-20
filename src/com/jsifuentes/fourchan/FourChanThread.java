package com.jsifuentes.fourchan;

import com.jsifuentes.Helper;
import com.jsifuentes.core.Configuration;
import com.jsifuentes.core.Output;
import com.jsifuentes.core.database.Database;
import org.json.*;
import sun.security.krb5.Config;

import java.net.URLEncoder;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.TimeZone;

/**
 * Created by Jacob on 11/17/2014.
 */
public class FourChanThread {
    private ArrayList<FourChanPost> Posts = new ArrayList<FourChanPost>();

    private String Board;
    private Long ThreadID;
    private Boolean Sticky = false;
    private Boolean Closed = false;
    private Boolean Archived = false;
    private Integer Replies = 0;
    private Integer Images = 0;
    private Boolean BumpLimit = false;
    private Boolean ImageLimit = false;
    private Long[] CapcodeReplies;
    private Long LastModified;
    private String Tag;
    private String SemanticURL;

    private final String BaseURL = "http://a.4cdn.org/";

    public String verify(String board, Long threadID) {
        String[] boardBlacklist = ((String)Configuration.get("archive.blacklist")).split("/");

        if(Arrays.asList(boardBlacklist).contains(board)) {
            return "Sorry, we currently don't allow this board to be archived.";
        }

        if(!this.retrieve(board, threadID)) {
            return "That thread does not exist! Did it 404?";
        }

        if(this.getPosts().size() < 10) {
            return "Threads must have at least 10 replies to be archived.";
        }

        return null;
    }

    public Boolean retrieve(String board, Long threadID) {
        String threadURL = this.BaseURL + board + "/thread/" + threadID.toString() + ".json";

        String threadJSON;
        try {
            threadJSON = Helper.readFromURL(threadURL);
        }
        catch(Exception exc) {
            return false;
        }

        if(threadJSON.equals("")) {
            return false;
        }

        JSONObject threadObject = new JSONObject(threadJSON);
        JSONArray threadPosts = (JSONArray)threadObject.get("posts");
        JSONObject threadOP = (JSONObject)threadPosts.get(0);

        this.Board = board;
        this.ThreadID = threadID;
        if(threadOP.has("sticky")) this.Sticky = threadOP.getInt("sticky") == 1;
        if(threadOP.has("closed")) this.Closed = threadOP.getInt("closed") == 1;
        if(threadOP.has("archived")) this.Archived = threadOP.getInt("archived") == 1;
        if(threadOP.has("replies")) this.Replies = threadOP.getInt("replies");
        if(threadOP.has("images")) this.Images = threadOP.getInt("images");
        if(threadOP.has("bumplimit")) this.BumpLimit = threadOP.getInt("bumplimit") == 1;
        if(threadOP.has("imagelimit")) this.ImageLimit = threadOP.getInt("imagelimit") == 1;
        if(threadOP.has("last_modified")) this.LastModified = threadOP.getLong("last_modified");
        //if(threadOP.containsKey("capcode_replies")) this.CapcodeReplies = (Boolean)threadOP.get("capcode_replies");

        for(int i = 0; i < threadPosts.length(); i = i + 1) {
            JSONObject threadPost = (JSONObject) threadPosts.get(i);

            FourChanPost postObject = new FourChanPost();
            postObject.setPostID(threadPost.getLong("no"));
            if (threadPost.has("id")) postObject.setUserID(threadPost.getString("id"));
            postObject.setName(threadPost.getString("name"));
            if (threadPost.has("subject")) postObject.setSubject(threadPost.getString("subject"));
            if (threadPost.has("capcode")) postObject.setCapcode(threadPost.getString("capcode"));
            if (threadPost.has("trip")) postObject.setTripcode(threadPost.getString("trip"));
            if (threadPost.has("com")) postObject.setComment(threadPost.getString("com"));
            postObject.setReplyTo(threadPost.getLong("resto"));
            postObject.setPostDate(threadPost.getString("now"));
            postObject.setSmallTimestamp(threadPost.getLong("time"));
            if (threadPost.has("country_name")) postObject.setCountryName(threadPost.getString("country_name"));
            if (threadPost.has("country")) postObject.setCountry(threadPost.getString("country"));
            if (threadPost.has("spoiler")) postObject.setSpoiler(threadPost.getInt("spoiler") == 1);

            // Images

            if (threadPost.has("tim")) postObject.setBigTimestamp(threadPost.getLong("tim")); // 4chan file name
            if (threadPost.has("ext")) postObject.setImageExtension(threadPost.getString("ext"));
            if (threadPost.has("filename")) postObject.setImageFilename(threadPost.getString("filename"));
            if (threadPost.has("fsize")) postObject.setImageSize(threadPost.getLong("fsize"));
            if (threadPost.has("w")) postObject.setImageWidth(threadPost.getInt("w"));
            if (threadPost.has("h")) postObject.setImageHeight(threadPost.getInt("h"));
            if (threadPost.has("md5")) postObject.setImageMD5(threadPost.getString("md5"));
            if (threadPost.has("tn_w")) postObject.setThumbWidth(threadPost.getInt("tn_w"));
            if (threadPost.has("tn_h")) postObject.setThumbHeight(threadPost.getInt("tn_h"));
            if (threadPost.has("filedeleted")) postObject.setFileDeleted(threadPost.getInt("filedeleted") == 1);

            this.Posts.add(postObject);
        }

        return true;
    }

    public Boolean archive(String requestedByIP) throws Exception {
        try(Connection conn = Database.getConnection()) {
            Boolean updateThread = false;
            Integer baseThreadID = 0;
            Integer postsSize = Posts.size();

            try(PreparedStatement getExistingThread = conn.prepareStatement("SELECT * FROM threads WHERE thread_id = ? AND board = ? LIMIT 1")) {
                getExistingThread.setLong(1, this.ThreadID);
                getExistingThread.setString(2, this.Board);

                try(ResultSet existingThreadResult = getExistingThread.executeQuery()) {
                    // Only one result
                    if(existingThreadResult.next()) {
                        if(existingThreadResult.getInt("busy") == 1) {
                            throw new Exception("Thread is currently busy.");
                        }
                        updateThread = true;
                        baseThreadID = existingThreadResult.getInt("id");
                    }
                }
            }

            Integer lastReplyPosition = 0;

            if(updateThread) {
                Long lastReplyID = this.Posts.get(this.Posts.size() - 1).getPostID();

                try(PreparedStatement getLastPost = conn.prepareStatement("SELECT SQL_CALC_FOUND_ROWS chan_id FROM posts WHERE threads_id = ? ORDER BY id DESC LIMIT 1")) {
                    getLastPost.setInt(1, baseThreadID);

                    try(ResultSet lastPostResult = getLastPost.executeQuery()) {
                        // Only one result
                        if(lastPostResult.next()) {
                            Long lastChanID = lastPostResult.getLong("chan_id");
                            if(lastChanID.equals(lastReplyID)) {
                                return true;
                            }
                        } else {
                            throw new Exception("Something weird happened: lastPostResult SQL is returning no results.");
                        }
                    }
                }

                try(Statement postsCount = conn.createStatement()) {
                    try(ResultSet postsCountResult = postsCount.executeQuery("SELECT FOUND_ROWS()")) {
                        if(postsCountResult.next()) {
                            lastReplyPosition = postsCountResult.getInt(1);
                        }
                    }
                }

                if(lastReplyPosition > postsSize) {
                    throw new Exception("Something weird happened: lastReplyPosition (" + lastReplyPosition + " is > Posts.size ( " + postsSize + ")");
                }

                try(PreparedStatement updateThreadStatement = conn.prepareStatement("UPDATE threads SET busy = 1, times_updated = times_updated + 1, update_date = NOW(), user_ips = CONCAT(ISNULL(user_ips, ''), ?), alive = 1 WHERE id = ?")) {
                    updateThreadStatement.setString(1, requestedByIP);
                    updateThreadStatement.setInt(2, baseThreadID);
                    if(updateThreadStatement.executeUpdate() < 1) {
                        throw new Exception("Failed to update thread in pre-archive!");
                    }
                }
            } else {
                try(PreparedStatement insertThreadStatement = conn.prepareStatement("INSERT INTO threads (thread_id, board, archive_date, user_ips, secret, busy) VALUES (?, ?, NOW(), ?, ?, 1)", Statement.RETURN_GENERATED_KEYS)) {
                    insertThreadStatement.setLong(1, this.ThreadID);
                    insertThreadStatement.setString(2, this.Board);
                    insertThreadStatement.setString(3, requestedByIP);
                    insertThreadStatement.setString(4, Helper.randomString(8));
                    if(insertThreadStatement.executeUpdate() < 1) {
                        throw new Exception("Failed to insert thread in pre-archive!");
                    }

                    ResultSet lastInsertKeys = insertThreadStatement.getGeneratedKeys();
                    if(lastInsertKeys.next()) {
                        baseThreadID = lastInsertKeys.getInt(1);
                    } else {
                        throw new Exception("Something weird happened. getGeneratedKeys() returned empty set on insert pre-archive!");
                    }
                }
            }

            // now we can actually archive the posts.


            // Initialize up here so we don't create a million of them.
            HashMap<String, String> imgurHeader = new HashMap<String, String>() {{
                put("Authorization", "Client-ID " + Configuration.get("keys.imgur"));
            }};

            try(PreparedStatement insertPost = conn.prepareStatement("INSERT INTO posts" +
                    "(chan_id, threads_id, body, subject, name, chan_image_name, image_size, thumb_dimensions," +
                    "image_dimensions, image_url, original_image_name, chan_user_id, tripcode," +
                    "capcode, imgur_hash, chan_post_date, available) VALUES" +
                    "(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, 1)")) {
                for (int pos = lastReplyPosition; pos < postsSize; pos++) {
                    FourChanPost currentPost = Posts.get(pos);

                    Long postID = currentPost.getPostID();
                    String uploadedImageUrl = null;
                    String uploadedImageDeleteHash = null;

                    if (currentPost.getImageSize() != null && currentPost.getImageSize() > 0) {
                        String imageLink = "http://i.4cdn.org/" + this.Board + "/src/" + currentPost.getBigTimestamp() + currentPost.getImageExtension();
                        Boolean doImageshack = false;

                        try {
                            String imgurResponse = Helper.sendPostRequest("https://api.imgur.com/3/image.json", "image=" + URLEncoder.encode(imageLink, "UTF-8"), imgurHeader, false);
                            if (imgurResponse.length() > 0) {
                                JSONObject imgurObject = new JSONObject(imgurResponse);
                                JSONObject imgurData = (JSONObject) imgurObject.get("data");
                                if ((int)imgurObject.get("status") == 200 && imgurData != null && imgurData.get("link") != null && imgurData.get("link") != imageLink) {
                                    uploadedImageUrl = (String) imgurData.get("link");
                                    uploadedImageDeleteHash = (String) imgurData.get("deletehash");
                                } else {
                                    doImageshack = true;
                                    if(imgurData != null && imgurData.has("error")) {
                                        Output.error("Imgur error: " + imgurObject.get("error"));
                                    }
                                }
                            } else {
                                doImageshack = true;
                            }
                        } catch(Exception exc) {
                            doImageshack = true;
                        }

                        try {
                            if (doImageshack) {
                                String imageshackResponse = Helper.sendPostRequest("https://post.imageshack.us/upload_api.php", "key=" + URLEncoder.encode((String)Configuration.get("keys.imageshack"), "UTF-8") + "&url=" + URLEncoder.encode(imageLink, "UTF-8") + "&format=json", null, false);
                                JSONObject imageshackObject = new JSONObject(imageshackResponse);
                                JSONObject imageshackLinks = (JSONObject) imageshackObject.get("links");
                                if (imageshackObject.get("status") == "1" && imageshackLinks.get("image_link") != null && imageshackLinks.get("image_link") != imageLink) {
                                    uploadedImageUrl = (String) imageshackLinks.get("image_link");
                                } else {
                                    uploadedImageUrl = (String) Configuration.get("archive.image_404");
                                }
                            }
                        } catch(Exception exc) {
                            uploadedImageUrl = null;
                            uploadedImageDeleteHash = null;
                        }
                    }

                    // add to database
                    insertPost.setLong(1, currentPost.getPostID());
                    insertPost.setLong(2, baseThreadID);

                    String comment = "";
                    if(currentPost.getComment() != null) {
                        comment = currentPost.getComment();
                    }
                    insertPost.setString(3, comment);
                    insertPost.setString(4, currentPost.getSubject());
                    insertPost.setString(5, currentPost.getName());

                    String chanImageName = null;
                    if(currentPost.getBigTimestamp() != null && currentPost.getImageExtension() != null) {
                        chanImageName = currentPost.getBigTimestamp() + currentPost.getImageExtension();
                    }

                    insertPost.setString(6, chanImageName);

                    Long imageSize = 0L;
                    if(currentPost.getImageSize() != null) {
                        imageSize = currentPost.getImageSize();
                    }
                    insertPost.setLong(7, imageSize);

                    String imageDimensions = null;
                    String thumbDimensions = null;
                    if(currentPost.getImageWidth() != null && currentPost.getImageHeight() != null) {
                        imageDimensions = currentPost.getImageWidth() + "x" + currentPost.getImageHeight();
                    }
                    if(currentPost.getThumbWidth() != null && currentPost.getThumbHeight() != null) {
                        thumbDimensions = currentPost.getThumbWidth() + "x" + currentPost.getThumbHeight();
                    }

                    insertPost.setString(8, thumbDimensions);
                    insertPost.setString(9, imageDimensions);
                    insertPost.setString(10, uploadedImageUrl);

                    String originalImageName = null;
                    if(currentPost.getImageFilename() != null && currentPost.getImageExtension() != null) {
                        originalImageName = currentPost.getImageFilename() + currentPost.getImageExtension();
                    }

                    insertPost.setString(11, originalImageName);
                    insertPost.setString(12, currentPost.getUserID());
                    insertPost.setString(13, currentPost.getTripcode());
                    insertPost.setString(14, currentPost.getCapcode());
                    insertPost.setString(15, uploadedImageDeleteHash);

                    Date date = new Date(currentPost.getSmallTimestamp()*1000L);
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    sdf.setTimeZone(TimeZone.getTimeZone("GMT-5")); // EST
                    String formattedDate = sdf.format(date);

                    insertPost.setString(16, formattedDate);
                    if(insertPost.executeUpdate() < 1) {
                        throw new Exception("Post ID " + currentPost.getPostID() + " failed to save in database!");
                    }
                }
            }

            try(PreparedStatement updateThreadStatement = conn.prepareStatement("UPDATE threads SET busy = 0 WHERE id = ?")) {
                updateThreadStatement.setInt(1, baseThreadID);
                if(updateThreadStatement.executeUpdate() < 1) {
                    throw new Exception("Failed to update thread in post-archive!");
                }
            }
        }
        return true;
    }

    public ArrayList<FourChanPost> getPosts() {
        return this.Posts;
    }

    public String getBoard() { return this.Board; }

    public Long getThreadID() {
        return this.ThreadID;
    }

    public Boolean getSticky() {
        return this.Sticky;
    }

    public Boolean getClosed() {
        return this.Closed;
    }

    public Boolean getArchived() {
        return this.Archived;
    }

    public Integer getReplies() {
        return this.Replies;
    }

    public Integer getImages() {
        return this.Images;
    }

    public Boolean getBumpLimit() {
        return this.BumpLimit;
    }

    public Boolean getImageLimit() {
        return this.ImageLimit;
    }

    public Long[] getCapcodeReplies() {
        return this.CapcodeReplies;
    }

    public Long getLastModified() {
        return this.LastModified;
    }

    public String getTag() {
        return this.Tag;
    }

    public String getSemanticURL() {
        return this.SemanticURL;
    }

    public String getBaseURL() {
        return this.BaseURL;
    }
}
