/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pixyel_backend.database;

import java.sql.SQLException;
import java.util.List;
import pixyel_backend.database.exceptions.CommentCreationException;
import pixyel_backend.database.objects.Comment;
import pixyel_backend.xml.XML;

/**
 *
 * @author Yannick
 */
public class BackendFunctions {

    private final DbConnection con;
    private final int userId;

    public BackendFunctions(DbConnection con, int userid) {
        this.con = con;
        this.userId = userid;
    }

    public Comment getComment(int commentId) throws CommentCreationException {
        return new Comment(commentId, this.con);
    }

    public void newComment(int pictureID, String comment) {
        System.out.println("test354");
        Comment.newComment(pictureID, this.userId, comment, this.con);
        System.out.println("test355");
    }

    public XML getCommentsForPicutre(int pictureId) throws SQLException, CommentCreationException {
        XML out = XML.createNewXML("commentsforpicture");
        out.addAttribute("pictureId", String.valueOf(pictureId));
        List<Comment> allComments = Comment.getCommentsForPicutre(pictureId, this.con);
        int iterator = 0;
        for (Comment currentComment : allComments) {
            out.addChild("comment");
            out.getChildren().get(iterator).addAttribute("CommentText", currentComment.getComment());
            out.getChildren().get(iterator).addAttribute("CreationDate", currentComment.getCommentDate().toString());
            out.getParent();
            iterator++;
        }
        return out;
    }

    public XML getPictures(int xCordinate, int yCordinate) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public void flagPicture(int pictureId) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public void uploadPicture(String picturedata) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    public void flagComment(int commentId) {
        
    }

    public void addNewComment(String text, int refersToPicuture) {
        Comment.newComment(refersToPicuture, userId, text, con);
    }

    

}
