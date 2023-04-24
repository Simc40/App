package com.simc.simc40.authentication;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.assertThat;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.isRoot;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.anyOf;
import static org.mockito.Mockito.mock;
//import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

import android.util.Log;
import android.view.View;

import androidx.test.espresso.PerformException;
import androidx.test.espresso.UiController;
import androidx.test.espresso.ViewAction;
import androidx.test.espresso.util.HumanReadables;
import androidx.test.espresso.util.TreeIterables;
import androidx.test.ext.junit.rules.ActivityScenarioRule;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.simc.simc40.R;

import junit.framework.TestCase;

import org.hamcrest.Matcher;
import org.junit.After;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
//import org.mockito.MockedStatic;
//import org.mockito.Mockito;
//import org.mockito.MockitoSession;
//import org.mockito.internal.matchers.Any;
//import org.mockito.junit.MockitoJUnitRunner;
//import org.mockito.junit.MockitoJUnitRunner;
//import org.powermock.api.mockito.PowerMockito;
//import org.powermock.core.classloader.annotations.PrepareForTest;
//import org.powermock.modules.junit4.PowerMockRunner;

//class FirebaseUser{
//
//    String uid;
//
//    FirebaseUser (String uid){
//        this.uid = uid;
//    }
//}




@RunWith(MockitoJUnitRunner.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class LoginSplashScreenTest extends TestCase {

//    Testar Cenários
//    1º Tem Sessão no FirebaseAuth ? (Não) , Tem Sessão no App (Não) -> Deve ir para a página de Login
//    3º Tem Sessão no FirebaseAuth ? (Não) , Tem Sessão no App (Sim) -> Deve ir para a página de Login
//    2º Tem Sessão no FirebaseAuth ? (Sim) , Tem Sessão no App (Não) -> Deve ir para a página de Login
//
//    Tem Sessão no FirebaseAuth ? (Sim) , Tem Sessão no App (Sim) -> Checar Permissões e Acesso no Database
//    Permissões e Acesso
//    4º Encontrou Usuário no Database ? (Não) -> Detectar erro (Não Exibir) e Ir para Login.
//    5º Encontrou chave de Acesso no banco ? (Não) ? -> Detectar erro (Não Exibir) e Ir para Login.
//    6º Encontrou chave de Permissão de App no banco ? (Não) ? -> Detectar erro (Não Exibir) e Ir para Login.
//    7º Encontrou chave de Todas as Permissões no banco ? (Não) ? -> Detectar erro (Não Exibir) e Ir para Login.
//
//    8º Acesso é Admin? (Sim) -> Ir para a Home.
//    9º Acesso é Responsavel? (Sim) -> Ir para a Home.
//    10º Acesso é Usuário ? (Sim), Tem Permissão de Acesso ao App (Não) -> Ir para o Login.
//    11º Acesso é Usuário ? (Sim), Tem Permissão de Acesso ao App (Sim) -> Ir para a Home.

    @BeforeClass
    public static void login1(){

        FirebaseAuth.getInstance().signInWithEmailAndPassword("julioclopes32@gmail.com", "12345678")
                .addOnSuccessListener(authResult -> Log.d("INSTTESTE", "BEFORECLASS"));
        await().atMost(3, TimeUnit.SECONDS);
    }

    @After
    public void login(){
        FirebaseAuth.getInstance().signInWithEmailAndPassword("julioclopes32@gmail.com", "12345678")
        .addOnSuccessListener(authResult -> Log.d("INSTTESTE", "AFTER"));
        await().atMost(3, TimeUnit.SECONDS);
    }

    @Rule
    public ActivityScenarioRule<LoginSplashScreen> activityRule = new ActivityScenarioRule<>(LoginSplashScreen.class);

    @Test
    public void AssertFirebaseUserIsNotNullAndReturnsExpectedUid(){
        String userUid = "teste";
        Log.d("INSTTESTE", "TESTE");

        FirebaseUser firebaseUser = spy(FirebaseUser.class);
        when(firebaseUser.getUid()).thenReturn(userUid);
        assertNotSame(FirebaseAuth.getInstance().getUid(), userUid);

    }

    @Test
    public void TestScenario01() { //1º Tem Sessão no FirebaseAuth ? (Não) , Tem Sessão no App (Não) -> Deve ir para a página de Login
        //FirebaseUser Null
        FirebaseAuth.getInstance().signOut();
        //Session Null
        Sessao session = mock(Sessao.class);
        when(session.getUidUsuarioDaSessao()).thenReturn(Sessao.USUARIO_NAO_ENCONTRADO);

        onView(withId(R.id.Login)).check(matches(isDisplayed()));
        assertNotNull(FirebaseAuth.getInstance());
    }

    @Test
    public void TestScenario02() { //2º Tem Sessão no FirebaseAuth ? (Não) , Tem Sessão no App (Sim) -> Deve ir para a página de Login
        //FirebaseUser Null
        FirebaseAuth.getInstance().signOut();

        //Session Valid
        Sessao session = mock(Sessao.class);
        when(session.getUidUsuarioDaSessao()).thenReturn("75A72HK1Q1WltVcAPNROtZg8uN02"); // Valid UID

        onView(withId(R.id.Login)).check(matches(isDisplayed()));
        assertNotNull(FirebaseAuth.getInstance());
    }

    @Test
    public void TestScenario03() { //3º Tem Sessão no FirebaseAuth ? (Sim) , Tem Sessão no App (Não) -> Deve ir para a página de Login
        assertNotNull(FirebaseAuth.getInstance());
        //FirebaseUser Valid
        //@Before

        //Session Null
        Sessao session = mock(Sessao.class);
        when(session.getUidUsuarioDaSessao()).thenReturn(Sessao.USUARIO_NAO_ENCONTRADO);


        onView(withId(R.id.Login)).check(matches(isDisplayed()));
    }

    @Test
    public void TestScenario04() {   //Tem Sessão no FirebaseAuth ? (Sim) , Tem Sessão no App (Sim) -> Checar Permissões e Acesso no Database
                                    //4º Encontrou Usuário no Database ? (Não) -> Ir para Login.
        assertNotNull(FirebaseAuth.getInstance());
        String invalidUid = "11111111111111111111";
        //FirebaseUser Valid (But Uid is not In Database)
        FirebaseUser firebaseUser = spy(FirebaseUser.class);
        when(firebaseUser.getUid()).thenReturn(invalidUid);

        //Session Valid
        Sessao session = mock(Sessao.class);
        when(session.getUidUsuarioDaSessao()).thenReturn(invalidUid); // Uid is not In Database

        onView(withId(R.id.Login)).check(matches(isDisplayed()));
    }

    @Test//(expected = FirebaseDatabaseException.class)
    public void TestScenario05() {   //5º Encontrou chave de Acesso no banco ? (Não) ? -> Detectar erro (Não Exibir) e Ir para Login.
        assertNotNull(FirebaseAuth.getInstance());
        String userUid = "user_null_acesso";
        //FirebaseUser Valid (But Uid is not In Database)
        FirebaseUser firebaseUser = spy(FirebaseUser.class);
        when(firebaseUser.getUid()).thenReturn(userUid);

        //Session Valid
        Sessao session = mock(Sessao.class);
        when(session.getUidUsuarioDaSessao()).thenReturn(userUid); // Uid is not In Database

        onView(withId(R.id.Login)).check(matches(isDisplayed()));
    }

    @Test//(expected = FirebaseDatabaseException.class)
    public void TestScenario06() {   //6º Encontrou chave de Permissão de App no banco ? (Não) ? -> Detectar erro (Não Exibir) e Ir para Login.
        assertNotNull(FirebaseAuth.getInstance());
        String userUid = "user_app_permission_null";
        //FirebaseUser Valid (But Uid is not In Database)
        FirebaseUser firebaseUser = spy(FirebaseUser.class);
        when(firebaseUser.getUid()).thenReturn(userUid);

        //Session Valid
        Sessao session = mock(Sessao.class);
        when(session.getUidUsuarioDaSessao()).thenReturn(userUid); // Uid is not In Database

        onView(withId(R.id.Login)).check(matches(isDisplayed()));
    }

    @Test//(expected = FirebaseDatabaseException.class)
    public void TestScenario07() {   //7º Encontrou chave de Todas as Permissões no banco ? (Não) ? -> Detectar erro (Não Exibir) e Ir para Login.
        assertNotNull(FirebaseAuth.getInstance());
        String userUid = "user_permissions_all_null";
        //FirebaseUser Valid (But Uid is not In Database)
        FirebaseUser firebaseUser = spy(FirebaseUser.class);
        when(firebaseUser.getUid()).thenReturn(userUid);

        //Session Valid
        Sessao session = mock(Sessao.class);
        when(session.getUidUsuarioDaSessao()).thenReturn(userUid); // Uid is not In Database

        onView(withId(R.id.Login)).check(matches(isDisplayed()));
    }

//
//    @Mock
//    sessionManagement session;
//
//    @Test
//    public void TestScenario08() throws Exception {   //8º Acesso é Admin? (Sim) -> Ir para a Home.
//        assertNotNull(FirebaseAuth.getInstance());
//        String userUid = "user_admin";
//
//        //FirebaseUser Valid
//        FirebaseUser firebaseUser = spy(FirebaseUser.class);
//        when(firebaseUser.getUid()).thenReturn(userUid);
//
//        //Session Valid
////        sessionManagement session = Mockito.mock(sessionManagement.class);
//        when(session.getSession()).thenReturn(userUid); // Uid is not In Database
//
////        await().atMost(5, TimeUnit.SECONDS);
//
//        onView(withId(R.id.Home)).check(matches(isDisplayed()));
//    }

//    @Test
//    public void TestScenario09() {   //9º Acesso é Responsavel? (Sim) -> Ir para a Home.
//        String userUid = "user_responsavel";
//        //FirebaseUser Valid
//        FirebaseUser firebaseUser = mock(FirebaseUser.class);
//        when(firebaseUser.getUid()).thenReturn(userUid);
//        FirebaseAuth firebaseAuth = mock(FirebaseAuth.class);
//        when(firebaseAuth.getCurrentUser()).thenReturn(firebaseUser);
//
//        //Session Valid
//        sessionManagement session = mock(sessionManagement.class);
//        when(session.getSession()).thenReturn(userUid); // Uid is not In Database
//
//        onView(withId(R.id.Home)).check(matches(isDisplayed()));
//    }
//
//    @Test
//    public void TestScenario10() {   //10º Acesso é Usuário ? (Sim), Tem Permissão de Acesso ao App (Não) -> Ir para o Login.
//        String userUid = "user_usuario_not_permitted";
//        //FirebaseUser Valid
//        FirebaseUser firebaseUser = mock(FirebaseUser.class);
//        when(firebaseUser.getUid()).thenReturn(userUid);
//        FirebaseAuth firebaseAuth = mock(FirebaseAuth.class);
//        when(firebaseAuth.getCurrentUser()).thenReturn(firebaseUser);
//
//        //Session Valid
//        sessionManagement session = mock(sessionManagement.class);
//        when(session.getSession()).thenReturn(userUid); // Uid is not In Database
//
//        onView(withId(R.id.Login)).check(matches(isDisplayed()));
//    }
//
//    @Test
//    public void TestScenario11() {   //11º Acesso é Usuário ? (Sim), Tem Permissão de Acesso ao App (Sim) -> Ir para a Home.
//        String userUid = "user_usuario_permitted";
//        //FirebaseUser Valid
//        FirebaseUser firebaseUser = mock(FirebaseUser.class);
//        when(firebaseUser.getUid()).thenReturn(userUid);
//        FirebaseAuth firebaseAuth = mock(FirebaseAuth.class);
//        when(firebaseAuth.getCurrentUser()).thenReturn(firebaseUser);
//
//        //Session Valid
//        sessionManagement session = mock(sessionManagement.class);
//        when(session.getSession()).thenReturn(userUid); // Uid is not In Database
//
//        onView(withId(R.id.Home)).check(matches(isDisplayed()));
//    }

    public static ViewAction waitId(final int viewId, final long millis) {
        return new ViewAction() {
            @Override
            public Matcher<View> getConstraints() {
                return isRoot();
            }

            @Override
            public String getDescription() {
                return "wait for a specific view with id <" + viewId + "> during " + millis + " millis.";
            }

            @Override
            public void perform(final UiController uiController, final View view) {
                uiController.loopMainThreadUntilIdle();
                final long startTime = System.currentTimeMillis();
                final long endTime = startTime + millis;
                final Matcher<View> viewMatcher = withId(viewId);

                do {
                    for (View child : TreeIterables.breadthFirstViewTraversal(view)) {
                        // found view with required ID
                        if (viewMatcher.matches(child)) {
                            return;
                        }
                    }

                    uiController.loopMainThreadForAtLeast(50);
                }
                while (System.currentTimeMillis() < endTime);

                // timeout happens
                throw new PerformException.Builder()
                        .withActionDescription(this.getDescription())
                        .withViewDescription(HumanReadables.describe(view))
                        .withCause(new TimeoutException())
                        .build();
            }
        };
    }
}