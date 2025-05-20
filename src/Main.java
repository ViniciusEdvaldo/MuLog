import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.*;
import javafx.util.Duration;

import javassist.*;
import javassist.expr.ExprEditor;
import javassist.expr.MethodCall;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.jar.*;
import java.util.zip.*;

public class Main extends Application {

    private TextArea logArea;
    private File ultimoDiretorio = null;
    private static final String CONFIG_FILE = "config.properties";
    private boolean modoClaro = false;
    private Label fraseLabel;
    private Timeline fraseTimeline;
    private boolean processamentoComErro = false;
    private List<File> dependenciasExternas = new ArrayList<>();

    private CheckBox cbOut = new CheckBox("System.out.println");
    private CheckBox cbErr = new CheckBox("System.err.println");
    private CheckBox cbStack = new CheckBox("printStackTrace");
    private CheckBox cbLogger = new CheckBox("java.util.logging");

    private final List<String> frases = Arrays.asList(
            "System.out.println é o novo debugger oficial!",
            "Log demais é igual fofoca... uma hora vaza!",
            "Se tem printStackTrace no código, tem emoção!",
            "Remover log é fácil, difícil é confiar que vai rodar sem ele.",
            "Código sem log é igual café sem açúcar: difícil de engolir.",
            "Se rodou de primeira... cadê os logs pra provar?",
            "Debug é quando você vê o que acontece. Log é quando você tenta adivinhar depois.",
            "Desliguei os logs e agora o bug sumiu... medo.",
            "Tem mais log que lógica nesse sistema!",
            "Se der erro, bota um println... clássico do desenvolvedor.",
            "Log é como diário de adolescente: ninguém lê, mas tem muito desabafo.",
            "Programador bom deixa log. O ótimo deixa sem bug.",
            "Não é bug, é feature... só tá mal logada.",
            "Mais fácil achar um bug sem log do que um dev sem café.",
            "Era uma vez um log que avisou e ninguém leu..."
    );

    @Override
    public void start(Stage stage) {
        stage.setTitle("MuLog - Removedor de Logs");
        carregarConfiguracoes();

        Label titleLabel = new Label("MuLog - Removedor de Logs");
        titleLabel.setStyle("-fx-font-size: 18px; -fx-text-fill: #A9FFA3; -fx-font-weight: bold;");

        Button selectJarButton = new Button("Selecionar JAR");
        selectJarButton.setPrefWidth(220);

        Button addDepsButton = new Button("Adicionar Dependências");
        addDepsButton.setPrefWidth(220);
        addDepsButton.setOnAction(e -> adicionarDependencias(stage));

        Button toggleThemeButton = new Button("Modo Claro");
        toggleThemeButton.setPrefWidth(100);
        toggleThemeButton.getStyleClass().add("theme-button");

        toggleThemeButton.setOnAction(e -> {
            modoClaro = !modoClaro;
            atualizarTema(stage.getScene(), titleLabel, selectJarButton, toggleThemeButton, addDepsButton);
        });

        HBox buttonsBox = new HBox(10, selectJarButton, addDepsButton);
        buttonsBox.setAlignment(Pos.CENTER);

        HBox topBox = new HBox();
        topBox.setAlignment(Pos.CENTER_RIGHT);
        topBox.getChildren().add(toggleThemeButton);
        HBox.setHgrow(topBox, Priority.ALWAYS);

        HBox checkboxBox = new HBox(10, cbOut, cbErr, cbStack, cbLogger);
        checkboxBox.setAlignment(Pos.CENTER);

        logArea = new TextArea();
        logArea.setPrefRowCount(10);
        logArea.setEditable(false);
        VBox.setVgrow(logArea, Priority.ALWAYS);

        fraseLabel = new Label();
        fraseLabel.setStyle("-fx-text-fill: #A9FFA3; -fx-font-style: italic;");

        selectJarButton.setOnAction(e -> handleJarProcessing(stage));

        Label logsLabel = new Label("Logs:");

        VBox vbox = new VBox(15, titleLabel, buttonsBox, checkboxBox, fraseLabel, logsLabel, logArea, topBox);
        vbox.setPadding(new Insets(20));
        vbox.setAlignment(Pos.CENTER);

        Scene scene = new Scene(vbox, 900, 600);
        stage.setScene(scene);
        atualizarTema(scene, titleLabel, selectJarButton, toggleThemeButton, addDepsButton);

        stage.show();

        iniciarFrasesRotativas();
    }

    private void iniciarFrasesRotativas() {
        fraseTimeline = new Timeline(
                new KeyFrame(Duration.seconds(3), event -> {
                    fraseLabel.setText(frases.get(new Random().nextInt(frases.size())));
                })
        );
        fraseTimeline.setCycleCount(Timeline.INDEFINITE);
        fraseTimeline.play();
    }

    private void adicionarDependencias(Stage stage) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Selecione as dependências JAR");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Arquivos JAR", "*.jar"));
        if (ultimoDiretorio != null && ultimoDiretorio.isDirectory()) {
            fileChooser.setInitialDirectory(ultimoDiretorio);
        }

        List<File> selectedFiles = fileChooser.showOpenMultipleDialog(stage);
        if (selectedFiles == null || selectedFiles.isEmpty()) return;

        dependenciasExternas.addAll(selectedFiles);
        log("Dependências adicionadas (" + selectedFiles.size() + "):", false);
        selectedFiles.forEach(file -> log(" - " + file.getName(), false));
    }

    private void handleJarProcessing(Stage stage) {
        if (dependenciasExternas.isEmpty()) {
            log("Aviso: Nenhuma dependência externa foi adicionada. Pode ocorrer erros ao processar JARs com dependências.", true);
        }

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Selecione o arquivo JAR");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Arquivos JAR", "*.jar"));
        if (ultimoDiretorio != null && ultimoDiretorio.isDirectory()) {
            fileChooser.setInitialDirectory(ultimoDiretorio);
        }
        File selectedJar = fileChooser.showOpenDialog(stage);
        if (selectedJar == null) return;

        ultimoDiretorio = selectedJar.getParentFile();
        salvarConfiguracoes();

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "Deseja salvar o JAR modificado?", ButtonType.YES, ButtonType.NO);
        confirm.setTitle("Salvar Modificado?");
        confirm.showAndWait();

        if (confirm.getResult() != ButtonType.YES) {
            log("Operação cancelada pelo usuário.", true);
            return;
        }

        DirectoryChooser dirChooser = new DirectoryChooser();
        dirChooser.setTitle("Selecione o diretório para salvar o JAR modificado");
        if (ultimoDiretorio != null && ultimoDiretorio.isDirectory()) {
            dirChooser.setInitialDirectory(ultimoDiretorio);
        }
        File outputDir = dirChooser.showDialog(stage);
        if (outputDir == null) {
            log("Diretório de saída não selecionado.", true);
            return;
        }

        ultimoDiretorio = outputDir;
        salvarConfiguracoes();

        processamentoComErro = false;
        logArea.clear();

        Task<Void> task = new Task<Void>() {
            @Override
            protected Void call() {
                try {
                    log("Processando: " + selectedJar.getName(), false);
                    File modifiedJar = processJar(selectedJar, outputDir);

                    if (processamentoComErro) {
                        log("Erros ocorreram durante o processamento. O JAR não foi salvo.", true);
                        return null;
                    }

                    if (modifiedJar == null) {
                        log("Erro: JAR não modificado", true);
                        return null;
                    }

                    log("Sucesso! JAR modificado salvo em:\n" + modifiedJar.getAbsolutePath(), false);
                } catch (Exception ex) {
                    log("Erro crítico: " + ex.getMessage(), true);
                    ex.printStackTrace();
                    processamentoComErro = true;
                }
                return null;
            }

            @Override
            protected void succeeded() {
                Platform.runLater(() -> {
                    fraseTimeline.stop();
                    if (processamentoComErro) {
                        fraseLabel.setText("Processamento concluído com erros!");
                        showAlert("Erro", "Ocorreram erros durante o processamento. Verifique os logs.");
                    } else {
                        fraseLabel.setText("Concluído com sucesso!");
                        mostrarJanelaDeSucesso();
                    }
                });
            }

            @Override
            protected void failed() {
                Platform.runLater(() -> {
                    fraseTimeline.stop();
                    fraseLabel.setText("Erro durante o processo.");
                    showAlert("Erro", "Falha ao processar o JAR.");
                });
            }
        };

        new Thread(task).start();
    }

    private File processJar(File jarFile, File outputDir) throws Exception {
        ClassPool pool = ClassPool.getDefault();
        pool.insertClassPath(jarFile.getAbsolutePath());

        for (File dep : dependenciasExternas) {
            try {
                pool.insertClassPath(dep.getAbsolutePath());
                log("Adicionada dependência: " + dep.getName(), false);
            } catch (NotFoundException e) {
                log("Erro ao adicionar dependência " + dep.getName() + ": " + e.getMessage(), true);
                processamentoComErro = true;
            }
        }

        File tempDir = Files.createTempDirectory("doal-temp").toFile();

        // Extrair MANIFEST.MF original se existir
        Manifest manifest = null;
        try (JarFile jar = new JarFile(jarFile)) {
            manifest = jar.getManifest();
        } catch (IOException e) {
            log("Erro ao ler MANIFEST.MF: " + e.getMessage(), true);
            processamentoComErro = true;
        }

        try (ZipFile zipFile = new ZipFile(jarFile)) {
            zipFile.stream()
                    .filter(entry -> entry.getName().endsWith(".class"))
                    .forEach(entry -> {
                        String className = entry.getName().replace("/", ".").replace(".class", "");
                        try {
                            CtClass ctClass = pool.get(className);
                            if (ctClass.isInterface() || ctClass.isEnum() || ctClass.isAnnotation()) return;
                            if (ctClass.isFrozen()) ctClass.defrost();

                            for (CtMethod method : ctClass.getDeclaredMethods()) {
                                method.instrument(new ExprEditor() {
                                    @Override
                                    public void edit(MethodCall m) throws CannotCompileException {
                                        String cn = m.getClassName();
                                        String mn = m.getMethodName();

                                        if (cbOut.isSelected() && cn.equals("java.io.PrintStream") && mn.equals("println")) {
                                            log("Removendo System.out.println em " + m.getEnclosingClass().getName(), false);
                                            m.replace("{}");
                                        }

                                        if (cbErr.isSelected() && cn.equals("java.io.PrintStream") && mn.equals("println") && m.toString().contains("System.err")) {
                                            log("Removendo System.err.println em " + m.getEnclosingClass().getName(), false);
                                            m.replace("{}");
                                        }

                                        if (cbStack.isSelected() && mn.equals("printStackTrace")) {
                                            log("Removendo printStackTrace em " + m.getEnclosingClass().getName(), false);
                                            m.replace("{}");
                                        }

                                        if (cbLogger.isSelected() && cn.startsWith("java.util.logging")) {
                                            log("Removendo java.util.logging em " + m.getEnclosingClass().getName(), false);
                                            m.replace("{}");
                                        }
                                    }
                                });
                            }

                            ctClass.writeFile(tempDir.getAbsolutePath());
                        } catch (Exception e) {
                            log("Erro ao processar: " + className + " - " + e.getMessage(), true);
                            processamentoComErro = true;
                        }
                    });
        } catch (Exception e) {
            log("Erro ao processar JAR: " + e.getMessage(), true);
            processamentoComErro = true;
            deleteDirectory(tempDir);
            return null;
        }

        if (processamentoComErro) {
            deleteDirectory(tempDir);
            return null;
        }

        File outputJar = new File(outputDir, "SemLog_" + jarFile.getName());
        try {
            createJar(outputJar, tempDir, jarFile, manifest);
            return outputJar;
        } catch (IOException e) {
            log("Erro ao criar JAR: " + e.getMessage(), true);
            processamentoComErro = true;
            deleteDirectory(tempDir);
            return null;
        }
    }

    private void createJar(File outputJar, File contentDir, File originalJar, Manifest manifest) throws IOException {
        try (JarOutputStream jos = manifest != null ?
                new JarOutputStream(new FileOutputStream(outputJar), manifest) :
                new JarOutputStream(new FileOutputStream(outputJar))) {

            // 1. Adicionar todos os recursos não-classes do original
            try (ZipFile originalZip = new ZipFile(originalJar)) {
                Enumeration<? extends ZipEntry> entries = originalZip.entries();
                while (entries.hasMoreElements()) {
                    ZipEntry entry = entries.nextElement();
                    if (!entry.getName().endsWith(".class") &&
                            !entry.getName().equalsIgnoreCase("META-INF/MANIFEST.MF")) {

                        jos.putNextEntry(new JarEntry(entry.getName()));
                        try (InputStream is = originalZip.getInputStream(entry)) {
                            byte[] buffer = new byte[4096];
                            int bytesRead;
                            while ((bytesRead = is.read(buffer)) != -1) {
                                jos.write(buffer, 0, bytesRead);
                            }
                        }
                        jos.closeEntry();
                    }

                }
            }

            Files.walk(contentDir.toPath())
                    .filter(path -> !Files.isDirectory(path))
                    .forEach(path -> {
                        String entryName = contentDir.toPath().relativize(path).toString()
                                .replace("\\", "/");
                        try {
                            jos.putNextEntry(new JarEntry(entryName));
                            Files.copy(path, jos);
                            jos.closeEntry();
                        } catch (IOException e) {
                            throw new UncheckedIOException(e);
                        }
                    });
        }
    }

    private void deleteDirectory(File dir) {
        if (dir.isDirectory()) {
            File[] files = dir.listFiles();
            if (files != null) {
                for (File file : files) {
                    deleteDirectory(file);
                }
            }
        }
        dir.delete();
    }

    private void carregarConfiguracoes() {
        Properties props = new Properties();
        File config = new File(CONFIG_FILE);
        if (config.exists()) {
            try (FileInputStream in = new FileInputStream(config)) {
                props.load(in);
                String path = props.getProperty("ultimoDiretorio");
                if (path != null) {
                    File dir = new File(path);
                    if (dir.exists() && dir.isDirectory()) {
                        ultimoDiretorio = dir;
                    }
                }
            } catch (IOException e) {
                System.out.println("Erro ao carregar configurações.");
            }
        }
    }

    private void salvarConfiguracoes() {
        Properties props = new Properties();
        if (ultimoDiretorio != null) {
            props.setProperty("ultimoDiretorio", ultimoDiretorio.getAbsolutePath());
        }
        try (FileOutputStream out = new FileOutputStream(CONFIG_FILE)) {
            props.store(out, "Configurações da aplicação");
        } catch (IOException e) {
            System.out.println("Erro ao salvar configurações.");
        }
    }

    private void atualizarTema(Scene scene, Label titleLabel, Button selectButton, Button toggleButton, Button depsButton) {
        VBox root = (VBox) scene.getRoot();
        if (modoClaro) {
            root.setStyle("-fx-background-color: #F5F5F5;");
            titleLabel.setStyle("-fx-font-size: 18px; -fx-text-fill: #2E8B57; -fx-font-weight: bold;");
            selectButton.setStyle("-fx-background-color: #2E8B57; -fx-text-fill: white;");
            depsButton.setStyle("-fx-background-color: #2E8B57; -fx-text-fill: white;");
            toggleButton.setText("Modo Escuro");
            toggleButton.setStyle("-fx-background-color: #E0E0E0; -fx-text-fill: #333;");
            logArea.setStyle("-fx-control-inner-background: white; -fx-text-fill: black;");
            fraseLabel.setStyle("-fx-text-fill: #333;");
            cbOut.setStyle("-fx-text-fill: black;");
            cbErr.setStyle("-fx-text-fill: black;");
            cbStack.setStyle("-fx-text-fill: black;");
            cbLogger.setStyle("-fx-text-fill: black;");
        } else {
            root.setStyle("-fx-background-color: #1E1E2F;");
            titleLabel.setStyle("-fx-font-size: 18px; -fx-text-fill: #A9FFA3; -fx-font-weight: bold;");
            selectButton.setStyle("-fx-background-color: #2E8B57; -fx-text-fill: white;");
            depsButton.setStyle("-fx-background-color: #2E8B57; -fx-text-fill: white;");
            toggleButton.setText("Modo Claro");
            toggleButton.setStyle("-fx-background-color: #333; -fx-text-fill: #A9FFA3;");
            logArea.setStyle("-fx-control-inner-background: #1E1E2F; -fx-text-fill: #90EE90;");
            fraseLabel.setStyle("-fx-text-fill: #A9FFA3;");
            cbOut.setStyle("-fx-text-fill: #A9FFA3;");
            cbErr.setStyle("-fx-text-fill: #A9FFA3;");
            cbStack.setStyle("-fx-text-fill: #A9FFA3;");
            cbLogger.setStyle("-fx-text-fill: #A9FFA3;");
        }
    }

    private void log(String message, boolean isError) {
        Platform.runLater(() -> {
            if (isError) {
                logArea.setStyle(logArea.getStyle() + "-fx-text-fill: #FF6B6B;");
                logArea.appendText("[ERRO] " + message + "\n");
                logArea.setStyle(logArea.getStyle().replace("-fx-text-fill: #FF6B6B;", ""));
            } else {
                logArea.appendText(message + "\n");
            }
        });
    }

    private void showAlert(String title, String message) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle(title);
            alert.setHeaderText(null);
            alert.setContentText(message);
            alert.showAndWait();
        });
    }

    private void mostrarJanelaDeSucesso() {
        Platform.runLater(() -> {
            Stage dialog = new Stage();
            dialog.setTitle("Sucesso!");

            Label label = new Label("Arquivo salvo com sucesso!");
            Button concluirBtn = new Button("Concluir");
            concluirBtn.setOnAction(e -> dialog.close());

            VBox layout = new VBox(15, label, concluirBtn);
            layout.setAlignment(Pos.CENTER);
            layout.setPadding(new Insets(20));

            Scene scene = new Scene(layout, 300, 150);
            dialog.setScene(scene);
            dialog.initModality(Modality.APPLICATION_MODAL);
            dialog.showAndWait();
        });
    }

    public static void main(String[] args) {
        launch(args);
    }
}
