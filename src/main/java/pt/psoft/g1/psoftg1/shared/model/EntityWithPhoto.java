package pt.psoft.g1.psoftg1.shared.model;

import java.nio.file.InvalidPathException;
import java.nio.file.Path;

public abstract class EntityWithPhoto {

    protected Photo photo;

    // Setter
    // This method is used by the mapper in order to set the photo. This will call
    // the setPhotoInternal method that
    // will contain all the logic to set the photo
    public void setPhoto(Photo photoURI) {
        setPhotoInternal(photoURI);
    }

    protected void setPhotoInternal(Photo photo) {
        setPhotoInternal(photo.getPhotoFile());
    }

    protected void setPhotoInternal(String photo) {
        if (photo == null) {
            this.photo = null;
        } else {
            try {
                // If the Path object instantiation succeeds, it means that we have a valid Path
                this.photo = new Photo(Path.of(photo));
            } catch (InvalidPathException e) {
                // For some reason it failed, let's set to null to avoid invalid references to
                // photos
                this.photo = null;
            }
        }
    }

    // Getter
    public Photo getPhoto() {
        return photo;
    }
}
