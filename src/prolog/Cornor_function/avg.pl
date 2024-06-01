avg_clause( List, Average ):-

    sum( List, Sum ),
    length( List, Length ),
    Length > 0,
    Average is Sum / Length.