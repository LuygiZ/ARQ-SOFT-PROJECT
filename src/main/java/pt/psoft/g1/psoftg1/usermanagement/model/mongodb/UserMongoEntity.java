/*
 * Copyright (c) 2022-2024 the original author or authors.
 *
 * MIT License
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and
 * associated documentation files (the "Software"), to deal in the Software without restriction,
 * including without limitation the rights to use, copy, modify, merge, publish, distribute,
 * sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or
 * substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT
 * NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package pt.psoft.g1.psoftg1.usermanagement.model.mongodb;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.annotation.Version;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import pt.psoft.g1.psoftg1.shared.model.mongodb.NameMongoEntity;
import pt.psoft.g1.psoftg1.usermanagement.model.Role;
import lombok.Getter;
import lombok.Setter;

@Profile("mongodb")
@Getter
@Setter
@Primary
@Document(collection = "users")
public class UserMongoEntity {

	@Id
	private String id; // ID interno (String) do MongoDB

	@Indexed(unique = true)
	@Field("user_id")
	private Long userId;

	@Version
	private Long version;

	// auditing info
	@CreatedDate
	@Field("created_at")
	private LocalDateTime createdAt;

	// auditing info
	@LastModifiedDate
	@Field("modified_at")
	private LocalDateTime modifiedAt;

	// auditing info
	@CreatedBy
	@Field("created_by")
	private String createdBy;

	// auditing info
	@LastModifiedBy
	@Field("modified_by")
	private String modifiedBy;

	private boolean enabled = true;

	@Field("username")
	@Email
	@NotNull
	@NotBlank
	private String username;

	@Field("password")
	@Getter
	@NotNull
	@NotBlank
	private String password;

	@Getter
	// @Setter
	@Field("name")
	private NameMongoEntity name;

	@Field("authorities")
	private final Set<Role> authorities = new HashSet<>();

	protected UserMongoEntity() {
		// for ORM only
	}

	public UserMongoEntity(
			String username,
			String password,
			LocalDateTime createdAt,
			LocalDateTime modifiedAt,
			String createdBy,
			String modifiedBy,
			boolean enabled,
			NameMongoEntity name,
			Set<Role> authorities) {

		this.username = username;
		this.password = password;
		this.createdAt = createdAt;
		this.modifiedAt = modifiedAt;
		this.createdBy = createdBy;
		this.modifiedBy = modifiedBy;
		this.enabled = enabled;
		this.name = name;
		this.authorities.addAll(authorities);
	}

	// @Override
	// public boolean isAccountNonExpired() {
	// return isEnabled();
	// }

	// @Override
	// public boolean isAccountNonLocked() {
	// return isEnabled();
	// }

	// @Override
	// public boolean isCredentialsNonExpired() {
	// return isEnabled();
	// }

}
