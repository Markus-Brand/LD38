package mbeb.lifeforms;

public enum SwordType {
	LONG_SWORD {
		@Override
		public String toString() {
			return "Sword";
		}
	},
	DAGGER {
		@Override
		public String toString() {
			return "Dagger";
		}
	},
	DAGGER_REVERSE {
		@Override
		public String toString() {
			return DAGGER.toString();
		}
	}
}
