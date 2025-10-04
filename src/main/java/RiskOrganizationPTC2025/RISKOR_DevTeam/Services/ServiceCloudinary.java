package RiskOrganizationPTC2025.RISKOR_DevTeam.Services;

import RiskOrganizationPTC2025.RISKOR_DevTeam.Models.DTO.DTOCloudinary;
import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Arrays;
import java.util.Map;
import java.util.UUID;

@Service
public class ServiceCloudinary {
    public ServiceCloudinary(Cloudinary cloudinary) {
        this.cloudinary = cloudinary;
    }

    public static final long MAX_FILE_SIZE = 5 * 1024 * 1024;

    private static final String[] ALLOWED_EXTENSIONS = {".jpg", ".png", ".jpeg", ".pdf"};

    private final Cloudinary cloudinary;

    //Método para enviar la imágen deseada a la carpeta que se especifique
    public DTOCloudinary uploadImage(MultipartFile file, String folder) throws IOException {
        validateImage(file);

        String originalFileName = file.getOriginalFilename();
        String ext = originalFileName.substring(originalFileName.lastIndexOf(".")).toLowerCase();
        String ct = file.getContentType() != null ? file.getContentType() : "";
        boolean isPdf = "application/pdf".equalsIgnoreCase(ct) || ".pdf".equals(ext);

        String unique = "file_" + UUID.randomUUID();
        //CLAVE: para PDF, public_id con extensión .pdf
        String publicId = isPdf ? (unique + ".pdf") : unique;

        Map<String, Object> options = ObjectUtils.asMap(
                "folder", folder,
                "public_id", publicId,
                "use_filename", false,
                "unique_filename", false,
                "resource_type", isPdf ? "raw" : "image"
        );
        if (!isPdf) {
            options.put("quality", "auto:good");
        }

        Map<?, ?> uploadResult = cloudinary.uploader().upload(file.getBytes(), options);
        String url      = (String) uploadResult.get("secure_url");  // …/raw/upload/.../file_xxx.pdf
        String publicIdOut = (String) uploadResult.get("public_id");
        return new DTOCloudinary(url, publicIdOut);
    }


    //Método para la eliminación de imágenes en caso la tabla requiera eliminar sus datos, exigiendo el nombre/carpeta de la img
    public void deleteByPublicId(String publicIdWithFolder) throws IOException {
        cloudinary.uploader().destroy(publicIdWithFolder, ObjectUtils.emptyMap()); //Eliminamos la imágen deseada de
    }

    public void validateImage(MultipartFile file){
        if (file.isEmpty()) throw new IllegalArgumentException("El archivo no puede estar vacio");
        if (file.getSize() > MAX_FILE_SIZE) throw new IllegalArgumentException("El tamaño del archivo no puede exceder los 5MB");
        String originalFileName = file.getOriginalFilename();
        if (originalFileName == null) throw new IllegalArgumentException("Nombre de archivo no valido");
        String extension = originalFileName.substring(originalFileName.lastIndexOf(".")).toLowerCase();
        if (!Arrays.asList(ALLOWED_EXTENSIONS).contains(extension)) throw new IllegalArgumentException("Solo se permiten archivos .jpg, .png, .jpeg, .pdf");
        String ct = file.getContentType();
        if (!(ct.startsWith("image/") || ct.equals("application/pdf"))) {
            throw new IllegalArgumentException("Solo se permiten imágenes o PDF");
        }
    }
}
