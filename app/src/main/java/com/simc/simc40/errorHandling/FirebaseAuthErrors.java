package com.simc.simc40.errorHandling;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class FirebaseAuthErrors {
    static Map<String, String[]> errorList = new HashMap<String, String[]>() {{
        put("ERROR_INVALID_CREDENTIAL", new String[]{"CREDENCIAIS INVÁLIDAS", "A credencial de autenticação fornecida está malformada ou expirou."});
        put("ERROR_INVALID_EMAIL", new String[]{"EMAIL INVÁLIDO", "O endereço de e-mail está mal formatado."});
        put("ERROR_WRONG_PASSWORD", new String[]{"SENHA ERRADA", "A senha é inválida ou o usuário não possui senha."});
        put("ERROR_USER_MISMATCH", new String[]{"ERRO DE USUÁRIO", "As credenciais fornecidas não correspondem ao usuário conectado anteriormente."});
        put("ERROR_REQUIRES_RECENT_LOGIN", new String[]{"REQUER LOGIN RECENTE", "Esta operação é sensível e requer autenticação recente. Faça login novamente antes de tentar novamente esta solicitação."});
        put("ERROR_ACCOUNT_EXISTS_WITH_DIFFERENT_CREDENTIAL" , new String[]{"A CONTA EXISTE COM CREDENCIAL DIFERENTE", "Já existe uma conta com o mesmo endereço de e-mail, mas credenciais de login diferentes. Faça login usando um provedor associado a este endereço de e-mail."});
        put("ERROR_EMAIL_ALREADY_IN_USE", new String[]{"EMAIL JÁ EM USO", "O endereço de e-mail já está em uso por outra conta."});
        put("ERROR_CREDENTIAL_ALREADY_IN_USE" , new String[]{"CREDENCIAL JÁ EM USO", "Esta credencial já está associada a uma conta de usuário diferente."});
        put("ERROR_USER_DISABLED", new String[]{"USUÁRIO DESABILITADO", "A conta do usuário não possui permissão ou foi desabilitada por um administrador."});
        put("ERROR_USER_TOKEN_EXPIRED", new String[]{"TOKEN DE USUÁRIO EXPIRADA", "A credencial do usuário não é mais válida. O usuário deve entrar novamente."});
        put("ERROR_USER_NOT_FOUND", new String[]{"USUÁRIO NÃO ENCONTRADO", "Não há registro de usuário correspondente a este email."});
        put("ERROR_INVALID_USER_TOKEN", new String[]{"CREDENCIAL DE USUÁRIO INVÁLIDA", "A credencial do usuário não é mais válida. O usuário deve entrar novamente."});
        put("ERROR_OPERATION_NOT_ALLOWED", new String[]{"OPERAÇÃO NÃO PERMITIDA", "Esta operação não é permitida. Você deve habilitar este serviço no console."});
        put("ERROR_WEAK_PASSWORD", new String[]{"SENHA FRACA", "A senha fornecida é inválida."});
        put("ERROR_MISSING_EMAIL", new String[]{"REQUER EMAIL", "Um endereço de e-mail deve ser fornecido."});
        put("ERROR_ACCESS_DENIED", new String[]{"ACESSO NEGADO", "Os seus privilégios de acesso ao Aplicativo estão inativos. Contate o seu administrador para requisitar acesso !"});
        put("ERROR_ACTIVITY_ACCESS_DENIED", new String[]{"ACESSO NEGADO", "Os seus privilégios de acesso a essa Atividade estão inativos. Contate o seu administrador para requisitar acesso !"});
    }};

    public static Map<String, String> getError(String errorCode){
        Map<String, String> response = new HashMap<>();
        if(errorList.get(errorCode) == null){
            response.put("errorCode", "Erro de Login");
            response.put("message", "Não foi possível determinar o problema, contate o suporte!");
            return response;
        }
        response.put("errorCode", Objects.requireNonNull(errorList.get(errorCode))[0]);
        response.put("message", Objects.requireNonNull(errorList.get(errorCode))[1]);
        return response;
    }
}

//API Docs

//("ERROR_INVALID_CUSTOM_TOKEN", "The custom token format is incorrect. Please check the documentation."));
//("ERROR_CUSTOM_TOKEN_MISMATCH", "The custom token corresponds to a different audience."));
//("ERROR_INVALID_CREDENTIAL", "The supplied auth credential is malformed or has expired."));
//("ERROR_INVALID_EMAIL", "The email address is badly formatted."));
//("ERROR_WRONG_PASSWORD", "The password is invalid or the user does not have a password."));
//("ERROR_USER_MISMATCH", "The supplied credentials do not correspond to the previously signed in user."));
//("ERROR_REQUIRES_RECENT_LOGIN", "This operation is sensitive and requires recent authentication. Log in again before retrying this request."));
//("ERROR_ACCOUNT_EXISTS_WITH_DIFFERENT_CREDENTIAL", "An account already exists with the same email address but different sign-in credentials. Sign in using a provider associated with this email address."));
//("ERROR_EMAIL_ALREADY_IN_USE", "The email address is already in use by another account."));
//("ERROR_CREDENTIAL_ALREADY_IN_USE", "This credential is already associated with a different user account."));
//("ERROR_USER_DISABLED", "The user account has been disabled by an administrator."));
//("ERROR_USER_TOKEN_EXPIRED", "The user\'s credential is no longer valid. The user must sign in again."));
//("ERROR_USER_NOT_FOUND", "There is no user record corresponding to this identifier. The user may have been deleted."));
//("ERROR_INVALID_USER_TOKEN", "The user\'s credential is no longer valid. The user must sign in again."));
//("ERROR_OPERATION_NOT_ALLOWED", "This operation is not allowed. You must enable this service in the console."));
//("ERROR_WEAK_PASSWORD", "The given password is invalid."));
//("ERROR_MISSING_EMAIL", "An email address must be provided.";