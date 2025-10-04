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
    /**
     * Sube imágenes y PDFs con resource_type="image".
     * - Para PDF fuerza public_id con .pdf para que la URL termine en .pdf
     */
    public DTOCloudinary uploadImage(MultipartFile file, String folder) throws IOException {
        validateFile(file);

        String original = file.getOriginalFilename();
        String ext = original != null ? original.substring(original.lastIndexOf(".")).toLowerCase() : "";
        boolean isPdf = ".pdf".equals(ext) || "application/pdf".equalsIgnoreCase(file.getContentType());

        String baseId = "file_" + UUID.randomUUID();
        String publicId = isPdf ? (baseId + ".pdf") : baseId;

        Map<String, Object> opts = ObjectUtils.asMap(
                "folder", folder,
                "public_id", publicId,
                "resource_type", "image", // ÚNICA RUTA
                "type", "upload",         // público
                "use_filename", false,
                "unique_filename", false
        );
        // Solo optimizamos imágenes; para PDF no aplica
        if (!isPdf) {
            opts.put("quality", "auto:good");
        }

        Map<?, ?> r = cloudinary.uploader().upload(file.getBytes(), opts);
        String url = (String) r.get("secure_url");   // p.ej. .../image/upload/.../file_xxx.pdf
        String pid = (String) r.get("public_id");
        return new DTOCloudinary(url, pid);
    }

    //Método para la eliminación de imágenes en caso la tabla requiera eliminar sus datos, exigiendo el nombre/carpeta de la img
    public void deleteByPublicId(String publicIdWithFolder) throws IOException {
        cloudinary.uploader().destroy(publicIdWithFolder, ObjectUtils.asMap("resource_type", "image")); //Eliminamos la el archivo deseado según su ID público
    }

    public void deleteByUrl(String url) {
        if (url == null || url.isBlank()) return;
        String pid = extractPublicIdFromUrlKeepExt(url);
        if (pid == null) return;

        // 1) image con extensión
        try { cloudinary.uploader().destroy(pid, ObjectUtils.asMap("resource_type","image")); } catch (Exception ignore) {}

        // 2) image sin extensión (por si el public_id no la tenía)
        String pidNoExt = pid.replaceAll("\\.[^./?]+$", "");
        if (!pidNoExt.equals(pid)) {
            try { cloudinary.uploader().destroy(pidNoExt, ObjectUtils.asMap("resource_type","image")); } catch (Exception ignore) {}
        }

        // 3) raw (por si quedaron PDFs antiguos subidos como raw)
        try { cloudinary.uploader().destroy(pid, ObjectUtils.asMap("resource_type","raw")); } catch (Exception ignore) {}
        if (!pidNoExt.equals(pid)) {
            try { cloudinary.uploader().destroy(pidNoExt, ObjectUtils.asMap("resource_type","raw")); } catch (Exception ignore) {}
        }
    }

    private void validateFile(MultipartFile file) {
        if (file.isEmpty()) throw new IllegalArgumentException("El archivo no puede estar vacío");
        if (file.getSize() > MAX_FILE_SIZE) throw new IllegalArgumentException("El archivo no puede exceder 5MB");

        String name = file.getOriginalFilename();
        if (name == null) throw new IllegalArgumentException("Nombre de archivo no válido");

        String ext = name.substring(name.lastIndexOf(".")).toLowerCase();
        if (!Arrays.asList(ALLOWED_EXTENSIONS).contains(ext))
            throw new IllegalArgumentException("Solo se permiten .jpg, .jpeg, .png, .pdf");

        String ct = file.getContentType() != null ? file.getContentType() : "";
        if (!(ct.startsWith("image/") || ct.equalsIgnoreCase("application/pdf")))
            throw new IllegalArgumentException("Solo se permiten imágenes o PDF");
    }

    /** Igual que tu método, pero SIN quitar la extensión .pdf */
    private String extractPublicIdFromUrlKeepExt(String url) {
        try {
            int i = url.indexOf("/upload/"); if (i < 0) return null;
            String after = url.substring(i + 8); // salta "/upload/"
            if (after.startsWith("v")) { int slash = after.indexOf("/"); if (slash > 0) after = after.substring(slash + 1); }
            int q = after.indexOf("?"); if (q > 0) after = after.substring(0, q);
            return after; // p.ej. RISKOR/Regulations-Documents/file_xxx.pdf
        } catch (Exception e) { return null; }
    }
}