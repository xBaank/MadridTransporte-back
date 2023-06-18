package busTrackerApi.routing.users

import kotlinx.html.*
import kotlinx.html.dom.createHTMLDocument
import kotlinx.html.dom.serialize

typealias ElementF = FlowContent.() -> Unit

private fun createEmailTemplate(title: ElementF, body: ElementF, link: ElementF) =
    createHTMLDocument().html {
        body {
            div {
                style = "font-family: Arial, sans-serif; background-color: #f4f4f4;"

                div {
                    style =
                        "max-width: 600px; margin: 0 auto; padding: 20px; background-color: #ffffff; border-radius: 4px; box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);"

                    div {
                        style = "text-align: center; margin-bottom: 20px;"
                        img(
                            src = "https://upload.wikimedia.org/wikipedia/commons/thumb/e/e6/Bus-logo.svg/1024px-Bus-logo.svg.png",
                            alt = "Logo"
                        ) {
                            style = "max-width: 150px; height: auto;"
                        }
                    }

                    title()
                    body()
                    link()

                    div {
                        style = "margin-top: 30px; text-align: center; font-size: 14px; color: #888888;"
                        p {
                            +"© 2023 BusTracker. All rights reserved."
                        }
                    }
                }
            }
        }
    }.serialize()


fun createVerifyTemplate(username: String, verifyUrl: String) = createEmailTemplate(
    {
        div {
            style = "text-align: center; margin-bottom: 30px;"
            h1 {
                style = "font-size: 24px; margin-bottom: 10px;"
                +"Welcome, $username!"
            }
        }
    },
    {
        div {
            style = "text-align: center; margin-bottom: 30px;"
            p {
                +"Thank you for creating an account! Please click the button below to verify your email address."
            }
        }
    }
) {
    div {
        style = "text-align: center;"
        a(
            href = verifyUrl
        ) {
            style =
                "display: inline-block; padding: 12px 24px; background-color: #007bff; color: #ffffff; text-decoration: none; border-radius: 4px;"
            +"Verify Email"
        }
    }
}

fun createResetPasswordEmailTemplate(username: String, redirectUrlWithToken: String) = createEmailTemplate(
    {
        div {
            style = "text-align: center; margin-bottom: 30px;"
            h1 {
                style = "font-size: 24px; margin-bottom: 10px;"
                +"Hello, $username!"
            }
        }
    },
    {
        div {
            style = "text-align: center; margin-bottom: 30px;"
            p {
                +"Please click the button below to reset your password."
            }
        }
    }, {
        div {
            style = "text-align: center;"
            a(
                href = redirectUrlWithToken
            ) {
                style =
                    "display: inline-block; padding: 12px 24px; background-color: #007bff; color: #ffffff; text-decoration: none; border-radius: 4px;"
                +"Reset Password"
            }
        }
    }
)