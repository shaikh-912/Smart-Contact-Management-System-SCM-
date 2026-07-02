package com.scm.project.services.serviceImplement;

import java.io.IOException;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.cloudinary.Cloudinary;
import com.cloudinary.Transformation;
import com.cloudinary.utils.ObjectUtils;
import com.scm.project.helper.AppConstants;
import com.scm.project.services.imageService;

@Service
public class imageImpl implements imageService {
	
	private Cloudinary cloudinary;
	public imageImpl(Cloudinary cloudinary) {
		this.cloudinary=cloudinary;
	}
	
	

	@Override
	public String uploadImage(MultipartFile contactImage,String filename) {
		
		try {
			byte[] data=new byte[contactImage.getInputStream().available()];
			contactImage.getInputStream().read(data);
			cloudinary.uploader().upload(data,ObjectUtils.asMap(
					"public_id",filename) );
			return this.getUrlFromPublicId(filename);
			
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
		
		
		
	}

	@Override
	public String getUrlFromPublicId(String publicId) {
		
		return cloudinary.url()
				.transformation(
						new Transformation<Transformation>()
						.width(AppConstants.CONTACT_IMAGE_WIDTH)
						.height(AppConstants.CONTACT_IMAGE_HEIGHT)
						.crop(AppConstants.CONTACT_IMAGE_CROP))
				.generate(publicId);
	}

}
