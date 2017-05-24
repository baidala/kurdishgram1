package org.vidogram.messenger.audioinfo.mp3;

public enum ID3v1Genre
{
  private final String description;

  static
  {
    Rap = new ID3v1Genre("Rap", 15, "Rap");
    Reggae = new ID3v1Genre("Reggae", 16, "Reggae");
    Rock = new ID3v1Genre("Rock", 17, "Rock");
    Techno = new ID3v1Genre("Techno", 18, "Techno");
    Industrial = new ID3v1Genre("Industrial", 19, "Industrial");
    Alternative = new ID3v1Genre("Alternative", 20, "Alternative");
    Ska = new ID3v1Genre("Ska", 21, "Ska");
    DeathMetal = new ID3v1Genre("DeathMetal", 22, "Death Metal");
    Pranks = new ID3v1Genre("Pranks", 23, "Pranks");
    Soundtrack = new ID3v1Genre("Soundtrack", 24, "Soundtrack");
    EuroTechno = new ID3v1Genre("EuroTechno", 25, "Euro-Techno");
    Ambient = new ID3v1Genre("Ambient", 26, "Ambient");
    TripHop = new ID3v1Genre("TripHop", 27, "Trip-Hop");
    Vocal = new ID3v1Genre("Vocal", 28, "Vocal");
    JazzFunk = new ID3v1Genre("JazzFunk", 29, "Jazz+Funk");
    Fusion = new ID3v1Genre("Fusion", 30, "Fusion");
    Trance = new ID3v1Genre("Trance", 31, "Trance");
    Classical = new ID3v1Genre("Classical", 32, "Classical");
    Instrumental = new ID3v1Genre("Instrumental", 33, "Instrumental");
    Acid = new ID3v1Genre("Acid", 34, "Acid");
    House = new ID3v1Genre("House", 35, "House");
    Game = new ID3v1Genre("Game", 36, "Game");
    SoundClip = new ID3v1Genre("SoundClip", 37, "Sound Clip");
    Gospel = new ID3v1Genre("Gospel", 38, "Gospel");
    Noise = new ID3v1Genre("Noise", 39, "Noise");
    AlternRock = new ID3v1Genre("AlternRock", 40, "AlternRock");
    Bass = new ID3v1Genre("Bass", 41, "Bass");
    Soul = new ID3v1Genre("Soul", 42, "Soul");
    Punk = new ID3v1Genre("Punk", 43, "Punk");
    Space = new ID3v1Genre("Space", 44, "Space");
    Meditative = new ID3v1Genre("Meditative", 45, "Meditative");
    InstrumentalPop = new ID3v1Genre("InstrumentalPop", 46, "Instrumental Pop");
    InstrumentalRock = new ID3v1Genre("InstrumentalRock", 47, "Instrumental Rock");
    Ethnic = new ID3v1Genre("Ethnic", 48, "Ethnic");
    Gothic = new ID3v1Genre("Gothic", 49, "Gothic");
    Darkwave = new ID3v1Genre("Darkwave", 50, "Darkwave");
    TechnoIndustrial = new ID3v1Genre("TechnoIndustrial", 51, "Techno-Industrial");
    Electronic = new ID3v1Genre("Electronic", 52, "Electronic");
    PopFolk = new ID3v1Genre("PopFolk", 53, "Pop-Folk");
    Eurodance = new ID3v1Genre("Eurodance", 54, "Eurodance");
    Dream = new ID3v1Genre("Dream", 55, "Dream");
    SouthernRock = new ID3v1Genre("SouthernRock", 56, "Southern Rock");
    Comedy = new ID3v1Genre("Comedy", 57, "Comedy");
    Cult = new ID3v1Genre("Cult", 58, "Cult");
    Gangsta = new ID3v1Genre("Gangsta", 59, "Gangsta");
    Top40 = new ID3v1Genre("Top40", 60, "Top 40");
    ChristianRap = new ID3v1Genre("ChristianRap", 61, "Christian Rap");
    PopFunk = new ID3v1Genre("PopFunk", 62, "Pop/Funk");
    Jungle = new ID3v1Genre("Jungle", 63, "Jungle");
    NativeAmerican = new ID3v1Genre("NativeAmerican", 64, "Native American");
    Cabaret = new ID3v1Genre("Cabaret", 65, "Cabaret");
    NewWave = new ID3v1Genre("NewWave", 66, "New Wave");
    Psychadelic = new ID3v1Genre("Psychadelic", 67, "Psychadelic");
    Rave = new ID3v1Genre("Rave", 68, "Rave");
    Showtunes = new ID3v1Genre("Showtunes", 69, "Showtunes");
    Trailer = new ID3v1Genre("Trailer", 70, "Trailer");
    LoFi = new ID3v1Genre("LoFi", 71, "Lo-Fi");
    Tribal = new ID3v1Genre("Tribal", 72, "Tribal");
    AcidPunk = new ID3v1Genre("AcidPunk", 73, "Acid Punk");
    AcidJazz = new ID3v1Genre("AcidJazz", 74, "Acid Jazz");
    Polka = new ID3v1Genre("Polka", 75, "Polka");
    Retro = new ID3v1Genre("Retro", 76, "Retro");
    Musical = new ID3v1Genre("Musical", 77, "Musical");
    RockAndRoll = new ID3v1Genre("RockAndRoll", 78, "Rock & Roll");
    HardRock = new ID3v1Genre("HardRock", 79, "Hard Rock");
    Folk = new ID3v1Genre("Folk", 80, "Folk");
    FolkRock = new ID3v1Genre("FolkRock", 81, "Folk-Rock");
    NationalFolk = new ID3v1Genre("NationalFolk", 82, "National Folk");
    Swing = new ID3v1Genre("Swing", 83, "Swing");
    FastFusion = new ID3v1Genre("FastFusion", 84, "Fast Fusion");
    Bebop = new ID3v1Genre("Bebop", 85, "Bebop");
    Latin = new ID3v1Genre("Latin", 86, "Latin");
    Revival = new ID3v1Genre("Revival", 87, "Revival");
    Celtic = new ID3v1Genre("Celtic", 88, "Celtic");
    Bluegrass = new ID3v1Genre("Bluegrass", 89, "Bluegrass");
    Avantgarde = new ID3v1Genre("Avantgarde", 90, "Avantgarde");
    GothicRock = new ID3v1Genre("GothicRock", 91, "Gothic Rock");
    ProgressiveRock = new ID3v1Genre("ProgressiveRock", 92, "Progressive Rock");
    PsychedelicRock = new ID3v1Genre("PsychedelicRock", 93, "Psychedelic Rock");
    SymphonicRock = new ID3v1Genre("SymphonicRock", 94, "Symphonic Rock");
    SlowRock = new ID3v1Genre("SlowRock", 95, "Slow Rock");
    BigBand = new ID3v1Genre("BigBand", 96, "Big Band");
    Chorus = new ID3v1Genre("Chorus", 97, "Chorus");
    EasyListening = new ID3v1Genre("EasyListening", 98, "Easy Listening");
    Acoustic = new ID3v1Genre("Acoustic", 99, "Acoustic");
    Humour = new ID3v1Genre("Humour", 100, "Humour");
    Speech = new ID3v1Genre("Speech", 101, "Speech");
    Chanson = new ID3v1Genre("Chanson", 102, "Chanson");
    Opera = new ID3v1Genre("Opera", 103, "Opera");
    ChamberMusic = new ID3v1Genre("ChamberMusic", 104, "Chamber Music");
    Sonata = new ID3v1Genre("Sonata", 105, "Sonata");
    Symphony = new ID3v1Genre("Symphony", 106, "Symphony");
    BootyBass = new ID3v1Genre("BootyBass", 107, "Booty Bass");
    Primus = new ID3v1Genre("Primus", 108, "Primus");
    PornGroove = new ID3v1Genre("PornGroove", 109, "Porn Groove");
    Satire = new ID3v1Genre("Satire", 110, "Satire");
    SlowJam = new ID3v1Genre("SlowJam", 111, "Slow Jam");
    Club = new ID3v1Genre("Club", 112, "Club");
    Tango = new ID3v1Genre("Tango", 113, "Tango");
    Samba = new ID3v1Genre("Samba", 114, "Samba");
    Folklore = new ID3v1Genre("Folklore", 115, "Folklore");
    Ballad = new ID3v1Genre("Ballad", 116, "Ballad");
    PowerBallad = new ID3v1Genre("PowerBallad", 117, "Power Ballad");
    RhytmicSoul = new ID3v1Genre("RhytmicSoul", 118, "Rhythmic Soul");
    Freestyle = new ID3v1Genre("Freestyle", 119, "Freestyle");
    Duet = new ID3v1Genre("Duet", 120, "Duet");
    PunkRock = new ID3v1Genre("PunkRock", 121, "Punk Rock");
    DrumSolo = new ID3v1Genre("DrumSolo", 122, "Drum Solo");
    ACapella = new ID3v1Genre("ACapella", 123, "A capella");
    EuroHouse = new ID3v1Genre("EuroHouse", 124, "Euro-House");
    DanceHall = new ID3v1Genre("DanceHall", 125, "Dance Hall");
    $VALUES = new ID3v1Genre[] { Blues, ClassicRock, Country, Dance, Disco, Funk, Grunge, HipHop, Jazz, Metal, NewAge, Oldies, Other, Pop, RnB, Rap, Reggae, Rock, Techno, Industrial, Alternative, Ska, DeathMetal, Pranks, Soundtrack, EuroTechno, Ambient, TripHop, Vocal, JazzFunk, Fusion, Trance, Classical, Instrumental, Acid, House, Game, SoundClip, Gospel, Noise, AlternRock, Bass, Soul, Punk, Space, Meditative, InstrumentalPop, InstrumentalRock, Ethnic, Gothic, Darkwave, TechnoIndustrial, Electronic, PopFolk, Eurodance, Dream, SouthernRock, Comedy, Cult, Gangsta, Top40, ChristianRap, PopFunk, Jungle, NativeAmerican, Cabaret, NewWave, Psychadelic, Rave, Showtunes, Trailer, LoFi, Tribal, AcidPunk, AcidJazz, Polka, Retro, Musical, RockAndRoll, HardRock, Folk, FolkRock, NationalFolk, Swing, FastFusion, Bebop, Latin, Revival, Celtic, Bluegrass, Avantgarde, GothicRock, ProgressiveRock, PsychedelicRock, SymphonicRock, SlowRock, BigBand, Chorus, EasyListening, Acoustic, Humour, Speech, Chanson, Opera, ChamberMusic, Sonata, Symphony, BootyBass, Primus, PornGroove, Satire, SlowJam, Club, Tango, Samba, Folklore, Ballad, PowerBallad, RhytmicSoul, Freestyle, Duet, PunkRock, DrumSolo, ACapella, EuroHouse, DanceHall };
  }

  private ID3v1Genre(String paramString)
  {
    this.description = paramString;
  }

  public static ID3v1Genre getGenre(int paramInt)
  {
    ID3v1Genre[] arrayOfID3v1Genre = values();
    if ((paramInt >= 0) && (paramInt < arrayOfID3v1Genre.length))
      return arrayOfID3v1Genre[paramInt];
    return null;
  }

  public String getDescription()
  {
    return this.description;
  }

  public int getId()
  {
    return ordinal();
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.vidogram.messenger.audioinfo.mp3.ID3v1Genre
 * JD-Core Version:    0.6.0
 */