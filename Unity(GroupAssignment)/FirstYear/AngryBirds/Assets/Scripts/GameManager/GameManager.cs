﻿using UnityEngine;
using System.Collections;

/**
 * This class defines the Game Manager. It receives information from all game objects and changes the game accordingly.
 * 
 * @author Group 9
 * 
 * */

public class GameManager : MonoBehaviour {

    private static GameManager instance;

    private GameState gameState;

    public delegate void OnGameStateChanged(GameState currentState);
    public static event OnGameStateChanged onGameStateChangedListener;

    private GameObject[] birdArray;
    private ArrayList pigArray;
    private GameObject[] materialArray;
    private int maxPoints;
    private Config CONFIG;

    // Update pauses game with the IEnumerator PauseGame
    void Update() {
        if (Input.GetKey(KeyCode.Escape) && Application.loadedLevelName != "StartScene" && Application.loadedLevelName != "EndScene") {
            StartCoroutine(PauseGame());            
        }
    }

    IEnumerator PauseGame() {
        if (Time.timeScale != 0) {
            Instantiate(Resources.Load("PauseGame/Level_Paused"), new Vector3(Camera.main.transform.position.x, Camera.main.transform.position.y, 0), new Quaternion(0, 0, 0, 0));
            Time.timeScale = 0;
            yield return new WaitForSeconds(1);
        }
    }

    // This generates the Game Manager Singleton

    void Awake() {
        gameState = GameState.GAME_INIT;

        if (instance != null && instance != this) {
            Destroy(this.gameObject);
            return;
        }
        instance = this;
        DontDestroyOnLoad(this.gameObject);
        CONFIG = GetComponent<Config>();
    }

    // The following methods defines game behaviour in the different states, and sets the different states through a delegate when necessary.

    public void SetLevel(string level) {
        //Debug.Log("Set Level!");
        pigArray = new ArrayList();
        Application.LoadLevel(level);
    }

    // This method gathers information about the objects in the game world so the game manager knows when the game is over. It then starts the game.

    public void SetGameInit() {
        if (!(Application.loadedLevelName == "EndScene")) {
            GameObject[] temp2 = GameObject.FindGameObjectsWithTag("Pig");
            materialArray = GameObject.FindGameObjectsWithTag("Material");

            foreach (GameObject go in temp2) {
                pigArray.Add(go);
            }
            //Debug.Log("Init!");
            //Debug.Log("Level points before delete:" + PlayerPrefs.GetInt(Application.loadedLevelName));
            //Debug.Log("HighScore on lvl:" + PlayerPrefs.GetInt(Application.loadedLevelName + "HighScore"));
            PlayerPrefs.SetInt(Application.loadedLevelName, 0);
            //Debug.Log("Level points after delete:" + PlayerPrefs.GetInt(Application.loadedLevelName));
            gameState = GameState.GAME_LEVEL_INIT;
            onGameStateChangedListener(gameState);
            birdArray = GameObject.Find("Shooter").GetComponent<ShooterAmmo>().Birds;


            float placementCounter = -7.467298f;

            for (int i = 0; i < birdArray.Length; i++) {
                birdArray[i] = Instantiate(birdArray[i], new Vector3(placementCounter, -6.265469f, 0), new Quaternion(0, 0, 0, 0)) as GameObject;
                placementCounter -= 1f;
                birdArray[i].GetComponent<AllBirdsScript>().GoTime = false;
            }
            maxPoints = CalculateMaxPoints();
            //Debug.Log("Init Done!");
        } else {
            gameState = GameState.GAME_LEVEL_INIT;
            onGameStateChangedListener(gameState);
        }
    }

    /* Waiting for user-input */
    public void SetGameWaiting() {
        // Debug.Log("Waiting!");
        gameState = GameState.GAME_LEVEL_WAITING;
        Shooter temp = GameObject.Find("Shooter").GetComponent<Shooter>();

        if (birdArray[0] != null && pigArray.Count > 0) {
            temp.Bird = birdArray[0];
            temp.Bird.GetComponent<AllBirdsScript>().GoTime = true;

            for (int i = 0; i < birdArray.Length - 1; i++) {
                birdArray[i] = birdArray[i + 1];
            }
            birdArray[birdArray.Length - 1] = null;
            onGameStateChangedListener(gameState);
        } else {
            SetGameCompleted();
        }
    }

    // This method tells all game objects that a bird is in the air.

    public void SetGameActive() {        
        gameState = GameState.GAME_LEVEL_ACTIVE;
        onGameStateChangedListener(gameState);
    }

    // This method takes received information and displays an appropriate screen for the player.

    public void SetGameCompleted() {
        //Debug.Log("Completed!");
        gameState = GameState.GAME_LEVEL_COMPLETED;
        onGameStateChangedListener(gameState);

        string levelName = Application.loadedLevelName;


        if (pigArray.Count == 0) {
            float waitTime = 2f;
            foreach (GameObject go in birdArray) {
                if (go != null) {
                    StartCoroutine(BirdSmoke(go, waitTime - 1));
                    StartCoroutine(WaitAndDie(go, waitTime));
                    waitTime += 2f;
                }
            }

            StartCoroutine(SpawnEndScreen(waitTime));
            PlayerPrefs.SetInt("levelCompleted", int.Parse(levelName.Substring(5, 1)));
        } else {
            foreach (GameObject go in birdArray) {
                DestroyObject(go);
            }
            Instantiate(Resources.Load("LevelFinish/Level_Failed"), new Vector3(-4, -3f, 0f), new Quaternion(0, 0, 0, 0));
        }
    }

    // Under follows various IEnumerators used to remove birds, generate animations and make the end screen.

    IEnumerator BirdSmoke(GameObject go, float waitTime) {
        yield return new WaitForSeconds(waitTime);
        GameObject birdPoints = (GameObject)Instantiate(Resources.Load("LevelFinish/BirdPoints"), new Vector3(go.transform.position.x + 1.2f, -5, -2), new Quaternion(0, 0, 0, 0));
        birdPoints.renderer.sortingLayerName = "Default";
        birdPoints.renderer.sortingOrder = 6;
        birdPoints.GetComponent<TextMesh>().text = ReturnScoreValue(go.name).ToString();
        go.GetComponent<AllBirdsScript>().Anim.SetBool("isDead", true);
        StartCoroutine(KillPoints(birdPoints, waitTime));
    }

    IEnumerator KillPoints(GameObject go, float waitTime) {
        yield return new WaitForSeconds(waitTime / 2);
        DestroyObject(go);
    }

    IEnumerator WaitAndDie(GameObject go, float waitTime) {
        yield return new WaitForSeconds(waitTime);
        OnGameObjectDie(go);
    }

    IEnumerator SpawnEndScreen(float waitTime) {
        yield return new WaitForSeconds(waitTime);
        //string levelName = Application.loadedLevelName;
        //int levelScore = GetScore(levelName);
        //Debug.Log(levelScore);
        //Debug.Log(PlayerPrefs.GetInt(levelName + "HighScore"));
        string level = Application.loadedLevelName;
        if (GetScore(level) > PlayerPrefs.GetInt(level + "HighScore")) {
            PlayerPrefs.SetInt(level + "HighScore", GetScore(level));
            PlayerPrefs.SetInt(level + "NewHighScore", 1);
        } else {
            PlayerPrefs.SetInt(level + "NewHighScore", 0);
        }

        Instantiate(Resources.Load("LevelFinish/Level_Cleared"), new Vector3(-4, -3f, 0f), new Quaternion(0, 0, 0, 0));
    }

    // Under follows various methods for transitions and information shring within the game.

    public void SetScore(string level, int score) {
        int value = GetScore(level);
        //Debug.Log("Level name: " + level);
        //Debug.Log("Score value: " + score);
        //Debug.Log("Value before adding: " + GetScore(level));
        PlayerPrefs.SetInt(level, value + score);
        //Debug.Log("Value after adding: " + GetScore(level));
    }

    public int GetScore(string level) {
        return PlayerPrefs.GetInt(level);
    }

    public int GetMaxScore() {
        return maxPoints;
    }

    public bool GetLevelAvailible(int level) {
        if (level <= PlayerPrefs.GetInt("levelCompleted") + 1) {
            return true;
        } else {
            return false;
        }
    }

    public void ShotFired() {
        SetGameActive();
    }

    public void PanDone() {
        // Debug.Log("Pan Done!");
        SetGameWaiting();
    }

    public void CameraReady() {
        // Debug.Log("Camera Ready!");
        SetGameInit();
    }

    public void ResetGame() {
        PlayerPrefs.DeleteAll();
    }

    public int CalculateMaxPoints() {
        int points = 0;

        foreach (GameObject go in birdArray) {
            points += ReturnScoreValue(go.name);
        }

        foreach (GameObject go in pigArray) {
            points += ReturnScoreValue(go.name);
        }

        foreach (GameObject go in materialArray) {
            points += ReturnScoreValue(go.name);
        }

        PlayerPrefs.SetInt(Application.loadedLevelName + "MaxScore", points);
        return points;
    }

    public void BirdStopped(GameObject o) {
        DestroyObject(o);
        SetGameWaiting();
    }

    public void OnGameObjectDie(GameObject gameObject) {
        SetScoreByInfo(gameObject.name);

        if (pigArray.Contains(gameObject)) {
            pigArray.Remove(gameObject);
            pigArray.TrimToSize();            
        }
       
        DestroyObject(gameObject);        
    }

    public static GameManager Instance {
        get { return instance; }
    }

    // The two following methods deliver points to the Game Manager based on information provided by the game designer in the attached Config script in the editor.
    // A more optimal way of doing this is probably the objects sending their own score, stored in their own script but we felt this was a more organized and easier
    // way to access it for a game designer.

    private void SetScoreByInfo(string name) {
        switch (name) {
            case "Pig": SetScore(Application.loadedLevelName, CONFIG.PIG); break;
            case "PigWithHelmet": SetScore(Application.loadedLevelName, CONFIG.PIG_WITH_HELMET); break;
            case "KingPig": SetScore(Application.loadedLevelName, CONFIG.KING_PIG); break;
            case "RedBird(Clone)": SetScore(Application.loadedLevelName, CONFIG.RED_BIRD); break;
            case "FatBird(Clone)": SetScore(Application.loadedLevelName, CONFIG.FAT_BIRD); break;
            case "OrangeBird(Clone)": SetScore(Application.loadedLevelName, CONFIG.ORANGE_BIRD); break;
            case "BlackBird(Clone)": SetScore(Application.loadedLevelName, CONFIG.BLACK_BIRD); break;
            case "BLOCK_WOOD": SetScore(Application.loadedLevelName, CONFIG.BLOCK_WOOD); break;
            case "CIRCLE_WOOD": SetScore(Application.loadedLevelName, CONFIG.CIRCLE_WOOD); break;
            case "RECTANGLE_WOOD": SetScore(Application.loadedLevelName, CONFIG.RECTANGLE_WOOD); break;
            case "SHORTESTRECTANGLE_WOOD": SetScore(Application.loadedLevelName, CONFIG.SHORTESTRECTANGLE_WOOD); break;
            case "SHORTRECTANGLE_WOOD": SetScore(Application.loadedLevelName, CONFIG.SHORTRECTANGLE_WOOD); break;
            case "SMALLBLOCK_WOOD": SetScore(Application.loadedLevelName, CONFIG.SMALLBLOCK_WOOD); break;
            case "TRIANGLE_WOOD": SetScore(Application.loadedLevelName, CONFIG.TRIANGLE_WOOD); break;
            case "TRIANGLE2_WOOD": SetScore(Application.loadedLevelName, CONFIG.TRIANGLE2_WOOD); break;
            case "SMALLBLOCK_STONE": SetScore(Application.loadedLevelName, CONFIG.SMALLBLOCK_STONE); break;
            case "SMALLESTBLOCK_STONE": SetScore(Application.loadedLevelName, CONFIG.SMALLESTBLOCK_STONE); break;
            case "BLOCK_STONE": SetScore(Application.loadedLevelName, CONFIG.BLOCK_STONE); break;
            case "BLOCK2_STONE": SetScore(Application.loadedLevelName, CONFIG.BLOCK2_STONE); break;
            case "SMALLCIRCLE_STONE": SetScore(Application.loadedLevelName, CONFIG.SMALLCIRCLE_STONE); break;
            case "CIRCLE_STONE": SetScore(Application.loadedLevelName, CONFIG.CIRCLE_STONE); break;
            case "RECTANGLE_STONE": SetScore(Application.loadedLevelName, CONFIG.RECTANGLE_STONE); break;
            case "SHORTESTRECTANGLE_STONE": SetScore(Application.loadedLevelName, CONFIG.SHORTESTRECTANGLE_STONE); break;
            case "SHORTRECTANGLE_STONE": SetScore(Application.loadedLevelName, CONFIG.SHORTRECTANGLE_STONE); break;
            case "LONGRECTANGLE_STONE": SetScore(Application.loadedLevelName, CONFIG.LONGRECTANGLE_STONE); break;
            case "FATRECTANGLE_STONE": SetScore(Application.loadedLevelName, CONFIG.FATRECTANGLE_STONE); break;
            case "TRIANGLE_STONE": SetScore(Application.loadedLevelName, CONFIG.TRIANGLE_STONE); break;
            case "TRIANGLE2_STONE": SetScore(Application.loadedLevelName, CONFIG.TRIANGLE2_STONE); break;
            case "BLOCK_GLASS": SetScore(Application.loadedLevelName, CONFIG.BLOCK_GLASS); break;
            case "SMALLBLOCK_GLASS": SetScore(Application.loadedLevelName, CONFIG.SMALLBLOCK_GLASS); break;
            case "SMALLESTBLOCK_GLASS": SetScore(Application.loadedLevelName, CONFIG.SMALLESTBLOCK_GLASS); break;
            case "CIRCLE_GLASS": SetScore(Application.loadedLevelName, CONFIG.CIRCLE_GLASS); break;
            case "SMALLCIRCLE_GLASS": SetScore(Application.loadedLevelName, CONFIG.SMALLCIRCLE_GLASS); break;
            case "FATRECTANGLE_GLASS": SetScore(Application.loadedLevelName, CONFIG.FATRECTANGLE_GLASS); break;
            case "RECTANGLE_GLASS": SetScore(Application.loadedLevelName, CONFIG.RECTANGLE_GLASS); break;
            case "SHORTRECTANGLE_GLASS": SetScore(Application.loadedLevelName, CONFIG.SHORTRECTANGLE_GLASS); break;
            case "SHORTESTRECTANGLE_GLASS": SetScore(Application.loadedLevelName, CONFIG.SHORTESTRECTANGLE_GLASS); break;
            case "LONGRECTANGLE_GLASS": SetScore(Application.loadedLevelName, CONFIG.LONGRECTANGLE_GLASS); break;
            case "TRIANGLE_GLASS": SetScore(Application.loadedLevelName, CONFIG.TRIANGLE_GLASS); break;
            case "TRIANGLE2_GLASS": SetScore(Application.loadedLevelName, CONFIG.TRIANGLE2_GLASS); break;
            default: break;
        }
    }

    private int ReturnScoreValue(string name) {
        switch (name) {
            case "Pig": return CONFIG.PIG;
            case "PigWithHelmet": return CONFIG.PIG_WITH_HELMET;
            case "KingPig": return CONFIG.KING_PIG;
            case "RedBird(Clone)": return CONFIG.RED_BIRD;
            case "FatBird(Clone)": return CONFIG.FAT_BIRD;
            case "OrangeBird(Clone)": return CONFIG.ORANGE_BIRD;
            case "BlackBird(Clone)": return CONFIG.BLACK_BIRD;
            case "BLOCK_WOOD": return CONFIG.BLOCK_WOOD;
            case "CIRCLE_WOOD": return CONFIG.CIRCLE_WOOD;
            case "RECTANGLE_WOOD": return CONFIG.RECTANGLE_WOOD;
            case "SHORTESTRECTANGLE_WOOD": return CONFIG.SHORTESTRECTANGLE_WOOD;
            case "SHORTRECTANGLE_WOOD": return CONFIG.SHORTRECTANGLE_WOOD;
            case "SMALLBLOCK_WOOD": return CONFIG.SMALLBLOCK_WOOD;
            case "TRIANGLE_WOOD": return CONFIG.TRIANGLE_WOOD;
            case "TRIANGLE2_WOOD": return CONFIG.TRIANGLE2_WOOD;
            case "SMALLBLOCK_STONE": return CONFIG.SMALLBLOCK_STONE;
            case "SMALLESTBLOCK_STONE": return CONFIG.SMALLESTBLOCK_STONE;
            case "BLOCK_STONE": return CONFIG.BLOCK_STONE;
            case "BLOCK2_STONE": return CONFIG.BLOCK2_STONE;
            case "SMALLCIRCLE_STONE": return CONFIG.SMALLCIRCLE_STONE;
            case "CIRCLE_STONE": return CONFIG.CIRCLE_STONE;
            case "RECTANGLE_STONE": return CONFIG.RECTANGLE_STONE;
            case "SHORTESTRECTANGLE_STONE": return CONFIG.SHORTESTRECTANGLE_STONE;
            case "SHORTRECTANGLE_STONE": return CONFIG.SHORTRECTANGLE_STONE;
            case "LONGRECTANGLE_STONE": return CONFIG.LONGRECTANGLE_STONE;
            case "FATRECTANGLE_STONE": return CONFIG.FATRECTANGLE_STONE;
            case "TRIANGLE_STONE": return CONFIG.TRIANGLE_STONE;
            case "TRIANGLE2_STONE": return CONFIG.TRIANGLE2_STONE;
            case "BLOCK_GLASS": return CONFIG.BLOCK_GLASS;
            case "SMALLBLOCK_GLASS": return CONFIG.SMALLBLOCK_GLASS;
            case "SMALLESTBLOCK_GLASS": return CONFIG.SMALLESTBLOCK_GLASS;
            case "CIRCLE_GLASS": return CONFIG.CIRCLE_GLASS;
            case "SMALLCIRCLE_GLASS": return CONFIG.SMALLCIRCLE_GLASS;
            case "FATRECTANGLE_GLASS": return CONFIG.FATRECTANGLE_GLASS;
            case "RECTANGLE_GLASS": return CONFIG.RECTANGLE_GLASS;
            case "SHORTRECTANGLE_GLASS": return CONFIG.SHORTRECTANGLE_GLASS;
            case "SHORTESTRECTANGLE_GLASS": return CONFIG.SHORTESTRECTANGLE_GLASS;
            case "LONGRECTANGLE_GLASS": return CONFIG.LONGRECTANGLE_GLASS;
            case "TRIANGLE_GLASS": return CONFIG.TRIANGLE_GLASS;
            case "TRIANGLE2_GLASS": return CONFIG.TRIANGLE2_GLASS;
            default: return 0;
        }
    }
}
