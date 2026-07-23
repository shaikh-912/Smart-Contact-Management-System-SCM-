package com.scm.project.services.serviceImplement;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.cloudinary.Cloudinary;
import com.cloudinary.Transformation;
import com.cloudinary.utils.ObjectUtils;
import com.scm.project.helper.AppConstants;
import com.scm.project.services.imageService;

@Service
public class imageImpl implements imageService {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	private final Cloudinary cloudinary;

	public imageImpl(Cloudinary cloudinary) {
		this.cloudinary = cloudinary;
	}

	@Override
	public String uploadImage(MultipartFile contactImage, String filename) {
		try {
			if (contactImage == null || contactImage.isEmpty()) {
				return null;
			}
			byte[] data = new byte[contactImage.getInputStream().available()];
			contactImage.getInputStream().read(data);
			cloudinary.uploader().upload(data, ObjectUtils.asMap("public_id", filename));
			return this.getUrlFromPublicId(filename);
		} catch (IOException e) {
			logger.error("Failed to upload image to Cloudinary: {}", e.getMessage());
			return null;
		}
	}

	@Override
	public String getUrlFromPublicId(String publicId) {
		return cloudinary.url()
				.transformation(
						new Transformation<Transformation<?>>()
								.width(AppConstants.CONTACT_IMAGE_WIDTH)
								.height(AppConstants.CONTACT_IMAGE_HEIGHT)
								.crop(AppConstants.CONTACT_IMAGE_CROP))
				.generate(publicId);
	}

	@Override
	public boolean deleteImage(String publicId) {
		if (publicId == null || publicId.isBlank()) {
			return false;
		}
		try {
			var result = cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
			String resultStatus = (String) result.get("result");
			boolean deleted = "ok".equalsIgnoreCase(resultStatus);
			if (!deleted) {
				logger.warn("Cloudinary delete returned non-ok status for publicId '{}': {}", publicId, resultStatus);
			}
			return deleted;
		} catch (IOException e) {
			logger.error("Failed to delete image from Cloudinary for publicId '{}': {}", publicId, e.getMessage());
			return false;
		}
	}
}

