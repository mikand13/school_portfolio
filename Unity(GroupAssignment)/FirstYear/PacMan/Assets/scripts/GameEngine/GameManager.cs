using UnityEngine;
using System.Collections;

public class GameManager : MonoBehaviour {
    // Singleton
    private static GameManager _instance;

    // GAME STATUS
    private uint totalScore;
    private uint score;
    private uint lives;
    public uint level;
	private uint ghostsEaten;

    // GAME LOGIC
    public enum Direction { North, South, West, East };
    private const uint SMALL_PILL_POINT = 10;
    private const uint LARGE_PILL_POINT = 50;
	private const uint EAT_GHOST_POINT = 200;
    private const uint POWER_UPS = 4;
	private const uint MAX_LEVEL = 2;
    public uint remainingPills;
    private uint powerUpEaten;

    // GAME OBJECTS
    public GameObject[] allPills;
    private ArrayList ghosts;
	private PacManMove pacMan;
	private GhostRandomMove shadow;
	private GhostRandomMove pokey;
	private GhostRandomMove speedy;
	private GhostRandomMove bashful;

    // GAME PHASE
    public enum GAME_STATE {CHASE, SCATTER, FRIGHTENED};
    public GAME_STATE current;
    public delegate void GameStateListener (GAME_STATE currentState);
    static public event GameStateListener stateListener;

    // SCATTER CHASE
    public float firstScatter = 7.0f;
    public float firstChase = 27.0f;
    public float secondScatter = 34.0f;
    public float secondChase = 54.0f;
    public float thirdScatter = 59.0f;
    public float thirdChase = 79.0f;
    public float fourthScatter = 84.0f;

    void Awake(){
<<<<<<< Updated upstream
		// Debug.Log("GameManager Running");
		if(_instance != null && _instance != this){
			// Debug.Log("GameManger Already Running, destroying");
=======
		Debug.Log("EnSingleton Awake");
		if(_instance != null && _instance != this){
			Debug.Log("EnSingleton Destructing ");
>>>>>>> Stashed changes
			Destroy(this.gameObject);
			return;
		}
		_instance = this;
		DontDestroyOnLoad(this.gameObject);
<<<<<<< Updated upstream
=======


		var c = GetComponents<MonoBehaviour>();
>>>>>>> Stashed changes
	}
	
	public static GameManager instance{ get{return _instance;} }

   	public PacManMove PacMan {
		get { return this.pacMan;}
		set { this.pacMan = value;}
	}

	public GhostRandomMove Shadow {
		get { return this.shadow;}
		set { this.shadow = value;}
	}

	public GhostRandomMove Pokey {
		get { return this.pokey;}
		set { this.pokey = value;}
	}

	public GhostRandomMove Speedy {
		get { return this.speedy;}
		set { this.speedy = value;}
	}
	
	public GhostRandomMove Bashful {
		get { return this.bashful;}
		set { this.bashful = value;}
	}

	public uint Score {
		get { return this.score;}
		set { this.score = value;}
	}

    public uint Level {
        get { return this.level; }
        set { this.level = value; }
    }

	public uint TotalScore {
		get { return this.totalScore;}
		set { this.totalScore = value;}
	}

	public uint Lives {
		get { return this.lives;}
		set { this.lives = value;}
	}

	public uint RemainingPills {
		get { return this.remainingPills;}
		set { this.remainingPills = value;}
	}

    public GameObject[] AllPills {
        get { return this.allPills; }
        set { this.allPills = value; }
    }

    public void Update() {
        if (Time.timeSinceLevelLoad >= firstScatter && Time.timeSinceLevelLoad <= firstChase) {
            current = GAME_STATE.CHASE;
        } else if (Time.timeSinceLevelLoad >= firstChase && Time.timeSinceLevelLoad <= secondScatter) {
            current = GAME_STATE.SCATTER;
        } else if (Time.timeSinceLevelLoad >= secondScatter && Time.timeSinceLevelLoad <= secondChase) {
            current = GAME_STATE.CHASE;
        } else if (Time.timeSinceLevelLoad >= secondChase && Time.timeSinceLevelLoad <= thirdScatter) {
            current = GAME_STATE.SCATTER;
        } else if (Time.timeSinceLevelLoad >= thirdScatter && Time.timeSinceLevelLoad <= thirdChase) {
            current = GAME_STATE.CHASE;
        } else if (Time.timeSinceLevelLoad >= thirdChase && Time.timeSinceLevelLoad <= fourthScatter) {
            current = GAME_STATE.SCATTER;
        } else if (Time.timeSinceLevelLoad >= fourthScatter) {
            current = GAME_STATE.CHASE;
        }
<<<<<<< Updated upstream

        if (stateListener != null) {
            stateListener(current);
        }
=======
        Debug.Log("GAME_STATE: " + current);
        stateListener(current);
>>>>>>> Stashed changes
    }

    public void GhostCollision(bool isVulnerable) {
        if (isVulnerable) {
			if (ghostsEaten == 0) {
				ghostsEaten ++;
			} else {
				ghostsEaten *= 2;
			}

            UpdateScore(EAT_GHOST_POINT * ghostsEaten);
        } else {
            lives--;

            if (lives == 0) {
                Application.LoadLevel("endMenu");
            }
			pacMan.resetPacMan();
			shadow.playerDied();
			pokey.playerDied();
			speedy.playerDied();
			bashful.playerDied();
        }
    }

    public void RemovePill(Collider pill) {
        remainingPills--;
        if (pill.tag == "powerUp") {
            powerUpEaten++;
            UpdateScore(LARGE_PILL_POINT);
			shadow.switchVulnerable();
			pokey.switchVulnerable();
			speedy.switchVulnerable();
			bashful.switchVulnerable();

            if (powerUpEaten == POWER_UPS) {
                pacMan.SuperMode = true;
                FollowCam follow = Camera.main.GetComponent<FollowCam>();
                follow.enabled = true;
                pacMan.superModeMusic();
            }
        } else {
            UpdateScore(SMALL_PILL_POINT);
        } if (remainingPills == 0) {
            if (level < MAX_LEVEL) {
                StartNewLevel();
            } else {
                Application.LoadLevel("endMenu");
            }	
        }
    }

    public void StartNewGame() {
        lives = 3;
        level = 1;
        score = 0;
        totalScore = 0;
        StartNewLevel();
    }

    private void StartNewLevel() {
        score = 0;
        powerUpEaten = 0;
        current = GAME_STATE.SCATTER;
        level++;

        Application.LoadLevel("level1");
    }

    public void UpdateScore(uint score) {
        this.score += score;
        this.totalScore += score;
    }
}
