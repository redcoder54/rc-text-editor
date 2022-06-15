package redcoder.rctexteditor.support;

import javafx.scene.image.Image;
import org.junit.Assert;
import org.junit.Test;

public class ImageResourcesTest {

    @Test
    public void getImage() {
        Image image = ImageResources.getImage("Copy16.gif");
        Assert.assertNotNull(image);
    }
}
