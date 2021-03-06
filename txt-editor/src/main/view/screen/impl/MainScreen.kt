package main.view.screen.impl

import javafx.geometry.Pos
import javafx.stage.Stage
import javafx.scene.Scene
import javafx.scene.control.*
import javafx.scene.input.KeyCode
import javafx.scene.input.KeyCodeCombination
import javafx.scene.input.KeyCombination
import javafx.scene.layout.BorderPane
import javafx.scene.layout.HBox
import javafx.scene.text.TextAlignment
import main.model.Editor
import main.util.Resources
import main.util.Settings
import main.view.handler.EditorFileHandler
import main.view.handler.EditorTabHandler
import main.view.listener.EditorTextListener

class MainScreen : AbstractScreen(600.0, 700.0, Settings.APP_NAME) {
    private val upperMenuBar = MenuBar()
    private val leftMenuBar = MenuBar()
    private val bottomBar = HBox()

    private val fileMenu = Menu()
    private val editMenu = Menu()
    private val searchMenu = Menu()
    private val helpMenu = Menu()

    private val fileMenuNew = MenuItem()
    private val fileMenuOpen = MenuItem()
    private val fileMenuSave = MenuItem()
    private val fileMenuSaveAs = MenuItem()
    private val fileMenuSaveAll = MenuItem()
    private val fileMenuCloseFile = MenuItem()

    private val editMenuUndo = MenuItem()
    private val editMenuRedo = MenuItem()
    private val editMenuSelectAll = MenuItem()
    private val editMenuClear = MenuItem()
    private val editMenuCut = MenuItem()
    private val editMenuCopy = MenuItem()
    private val editMenuPaste = MenuItem()

    private val searchMenuFindReplace = MenuItem()
    private val helpMenuAbout = MenuItem()

    private val caretPosition = Label()
    private val fileExtension = Label()
    private val fileCharset = Label()

    val editorFileHandler = EditorFileHandler(editorController, fileController)
    val tabPane: TabPane = TabPane()

    override fun start(primaryStage: Stage) {
        stage = primaryStage
        initComponents(primaryStage)

        primaryStage.scene = initScene()
        primaryStage.title = screenName
        primaryStage.icons.add(Resources.appIcon)
        primaryStage.isMaximized = true

        editorFileHandler.primaryStage = primaryStage
        editorFileHandler.root = this

        primaryStage.show()
    }

    override fun initScene(): Scene {
        val pane = BorderPane()

        pane.top = upperMenuBar
        pane.left = leftMenuBar
        pane.center = tabPane
        pane.bottom = bottomBar

        val scene = Scene(pane,  this.screenHeight, this.screenWidth)

        pane.prefHeightProperty().bind(scene.heightProperty())
        pane.prefWidthProperty().bind(scene.widthProperty())

        return scene
    }

    override fun initComponents(primaryStage: Stage) {
        initMenus()
        initBoxes()

        linkEditorHandlers()
        linkMenuItemHandlers()

        tabPane.tabs.addAll(editorController.getAllTabs()!!)
    }

    private fun initBoxes() {
        initLabel(caretPosition,  "Ln 1 Col 1", TextAlignment.RIGHT)
        initLabel(fileCharset,  "UTF-8", TextAlignment.RIGHT)
        initLabel(fileExtension,  "Plain Text", TextAlignment.RIGHT)

        initHBox(bottomBar, 35.0, Pos.BOTTOM_RIGHT, caretPosition, fileCharset, fileExtension)
    }

    private fun initMenus() {
        // File menu
        initMenuItem(fileMenuNew, fileMenu, "New", KeyCodeCombination(KeyCode.N, KeyCombination.CONTROL_DOWN))
        addSeparator(fileMenu)
        initMenuItem(fileMenuOpen, fileMenu, "Open", KeyCodeCombination(KeyCode.O, KeyCombination.CONTROL_DOWN))
        initMenuItem(fileMenuSave, fileMenu, "Save", KeyCodeCombination(KeyCode.S, KeyCombination.CONTROL_DOWN))
        initMenuItem(fileMenuSaveAs, fileMenu, "Save As...", KeyCodeCombination(KeyCode.S, KeyCombination.SHIFT_DOWN, KeyCombination.CONTROL_DOWN))
        addSeparator(fileMenu)
        initMenuItem(fileMenuCloseFile, fileMenu, "Close Tab", KeyCodeCombination(KeyCode.W, KeyCombination.CONTROL_DOWN))
        initMenuItem(fileMenuSaveAll, fileMenu, "Save All", KeyCodeCombination(KeyCode.S, KeyCombination.ALT_DOWN, KeyCombination.CONTROL_DOWN))

        // Edit menu
        initMenuItem(editMenuUndo, editMenu, "Undo", KeyCodeCombination(KeyCode.Z, KeyCombination.CONTROL_DOWN))
        initMenuItem(editMenuRedo, editMenu, "Redo", KeyCodeCombination(KeyCode.Y, KeyCombination.CONTROL_DOWN))
        addSeparator(editMenu)
        initMenuItem(editMenuSelectAll,  editMenu, "Select All", KeyCodeCombination(KeyCode.A, KeyCombination.CONTROL_DOWN))
        initMenuItem(editMenuClear,  editMenu, "Clear")
        addSeparator(editMenu)
        initMenuItem(editMenuCopy,  editMenu, "Copy", KeyCodeCombination(KeyCode.C, KeyCombination.CONTROL_DOWN))
        initMenuItem(editMenuPaste,  editMenu, "Paste", KeyCodeCombination(KeyCode.V, KeyCombination.CONTROL_DOWN))
        initMenuItem(editMenuCut,  editMenu, "Cut", KeyCodeCombination(KeyCode.X, KeyCombination.CONTROL_DOWN))

        //Search Menu
        initMenuItem(searchMenuFindReplace,  searchMenu, "Find", KeyCodeCombination(KeyCode.F, KeyCombination.CONTROL_DOWN))

        // Help menu
        initMenuItem(helpMenuAbout, helpMenu, "About")

        initMenu(fileMenu, upperMenuBar, "File")
        initMenu(editMenu, upperMenuBar, "Edit")
        initMenu(searchMenu, upperMenuBar, "Search")
        initMenu(helpMenu, upperMenuBar, "Help")
    }

    fun addTab(tabPane: TabPane, editor: Editor) {
        editorController.add(editor)
        tabPane.tabs.add(editor.tab)
        linkEditorHandlers(editor)
    }

    private fun linkMenuItemHandlers() {
        fileMenuNew.setOnAction {
            editorFileHandler.newFileRequest()
        }
        fileMenuOpen.setOnAction {
            editorFileHandler.openFileRequest()
        }
        fileMenuSave.setOnAction {
            editorFileHandler.saveFileRequest()
        }
        fileMenuSaveAs.setOnAction {
            editorFileHandler.saveAsFileRequest()
        }
        fileMenuSaveAll.setOnAction {
            editorFileHandler.saveAllFilesRequest()
        }
        fileMenuCloseFile.setOnAction {
            editorFileHandler.closeFileRequest()
        }
        searchMenuFindReplace.setOnAction {
            SearchScreen().startScreen(this)
        }
        helpMenuAbout.setOnAction {
            AboutScreen().startScreen(this)
        }

    }

    private fun linkEditorHandlers() {
        for (editor in editorController.editors!!) linkEditorHandlers(editor)
    }

    private fun linkEditorHandlers(editor: Editor) {
        editor.textAreaListener = EditorTextListener.listen(this)
        editor.onSelectRequest = EditorTabHandler.onSelectionRequest(this)
        editor.onCloseRequest = EditorTabHandler.onCloseRequest(this)
    }

    fun updateCaretPosition(lines: Int, columns: Int) {
        caretPosition.text = "Ln $lines, Col $columns"
    }

    fun updateFileCharsetLabel(charset: String) {
        fileCharset.text = charset
    }

    fun updateFileExtensionLabel(extension: String) {
        fileExtension.text = extension
    }
}