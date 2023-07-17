# User

## Register
To create a user account you need to send a POST request to the following URL:
`{url}/v1/user/register?backUrl={backUrl}&redirectUrl={redirectUrl}`
- `backUrl`: The Host of the back.
- `redirectUrl`: The URL where you will be redirected.

The body of the request must be a JSON object with the following fields:
- `username`: The username of the user.
- `password`: The password of the user.
- `email`: The email of the user.

The response code will be `201` or `409` or `400`.

This will email the user with a link to activate the account in the format of `{backUrl}/v1/users/verify?token={verifyToken}&redirectUrl={redirectUrl}`.

## Verify
This endpoint is used to verify a user account, it is sent to the email specified during the register.
To verify a user account you need to send a GET request to the following URL:
`{url}/v1/user/verify?token={verifyToken}&redirectUrl={redirectUrl}`

The response code will be `308` or `400`.

## Login
Once the verification is done, the user can log in to the system.
To log in you need to send a POST request to the following URL:
`{url}/v1/user/login`

The body of the request must be a JSON object with the following fields:
- `email`: The email of the user.
- `password`: The password of the user.

The response code will be `200` or `401` or `400`.
The response will contain a JSON object with the following fields:
- `token`: The Bearer token to authenticate with.

## Reset Password
If the user forgets the password, he can reset it.

First step is to send a POST request to the following URL:
`{url}/v1/user/send-reset-password?redirectUrl={redirectUrl}`

The body of the request must be a JSON object with the following fields:
- `email`: The email of the user.

This will email the user with a link to reset the password in the format of `{redirectUrl}?token={resetPasswordToken}`.

This will call the `front` passing the required token to reset the password.

Second step is to send a PUT request to the following URL:
`{url}/v1/user/reset-password?token={resetPasswordToken}`

The body of the request must be a JSON object with the following fields:
- `password`: The new password of the user.

The response code will be `200` or `401` or `400`.